package com.maximilianwiegmann.backend.notifications;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maximilianwiegmann.backend.account.AccountData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getNotifications() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(notificationService.getNotifications(accountData));
    }

}
