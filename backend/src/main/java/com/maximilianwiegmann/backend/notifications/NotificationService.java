package com.maximilianwiegmann.backend.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.account.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    public ResponseEntity<?> sendNotification(String uid, Notification notification) {
        AccountData accountData = accountRepository.findById(uid).orElse(null);
        if (accountData == null)
            return ResponseEntity.badRequest().build();
        notification.setAccountData(accountData);
        notificationRepository.save(notification);

        accountData.getNotifications().add(notification);
        accountRepository.save(accountData);
        return ResponseEntity.ok().build();
    }

    public List<Notification> getNotifications(AccountData accountData) {
        return notificationRepository.findByAccountDataId(accountData.getId());
    }

}
