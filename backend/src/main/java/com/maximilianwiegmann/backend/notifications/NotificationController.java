package com.maximilianwiegmann.backend.notifications;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maximilianwiegmann.backend.account.AccountData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationHandler notificationHandler;

    @GetMapping
    public ResponseEntity<?> getNotifications() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(notificationHandler.getNotifications(accountData));
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<?> readNotification(@PathVariable String id) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(notificationHandler.readNotification(accountData, id));
    }

    @PostMapping("/readAll")
    public ResponseEntity<?> readAllNotifications() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(notificationHandler.readAllNotifications(accountData));
    }

    @PostMapping("/notifyAll")
    public ResponseEntity<?> notifyAll(@RequestBody Notification notification) {
        notificationHandler.notifyAll(notification);
        return ResponseEntity.ok().build();
    }

}
