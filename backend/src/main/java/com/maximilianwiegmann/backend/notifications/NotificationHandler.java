package com.maximilianwiegmann.backend.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.account.AccountRepository;
import com.maximilianwiegmann.backend.security.config.JwtService;
import com.maximilianwiegmann.backend.security.token.TokenRepository;

import ch.qos.logback.core.subst.Token;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {

    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpUserRegistry userRegistry;
    private final JwtService jwtService;

    public void notifyUser(String uid, Notification notification) {
        AccountData accountData = accountRepository.findById(uid).orElse(null);
        if (accountData == null)
            return;
        notification.setUId(uid);
        notificationRepository.save(notification);
        
        SimpUser user = userRegistry.getUsers().stream().filter(entry -> {
            String id = jwtService.extractUId(entry.getName());
            return id != null && id.equals(uid);
        }).findFirst().orElse(null);

        if (user == null)
            return;

        messagingTemplate.convertAndSend("/notifications/" + user.getName(), notification.toJson());
    }
    
    public void notifyAll(Notification notification) {
        userRegistry.getUsers().forEach(user -> {
            messagingTemplate.convertAndSend("/notifications/" + user.getName(), notification.toJson());
        });
    }

    public ResponseEntity<List<Notification>> readAllNotifications(AccountData accountData) {
        List<Notification> notifications = notificationRepository.findByUserIdAndNotRead(accountData.getId());
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        return ResponseEntity.ok(getNotifications(accountData));
    }

    public ResponseEntity<List<Notification>> readNotification(AccountData accountData, String id) {
        List<Notification> notifications = notificationRepository.findByUserId(accountData.getId());
        notifications.stream().filter(notification -> notification.getId().equals(id)).findFirst().ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
        return ResponseEntity.ok(notifications);
    }

    public List<Notification> getNotifications(AccountData accountData) {
        return notificationRepository.findByUserId(accountData.getId());
    }

}
