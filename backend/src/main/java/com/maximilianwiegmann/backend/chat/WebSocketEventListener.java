package com.maximilianwiegmann.backend.chat;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators.Not;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.notifications.Notification;
import com.maximilianwiegmann.backend.notifications.NotificationHandler;

import java.util.Iterator;
import java.util.Objects;

import static java.lang.String.format;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private NotificationHandler notificationHandler;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection.");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String uId = (String) headerAccessor.getSessionAttributes().get("uId");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
        if (uId != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setUserId(uId);

            messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
        }
    }

}
