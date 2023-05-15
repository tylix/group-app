package com.maximilianwiegmann.backend.notifications;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    public ResponseEntity<?> sendNotification(String uid, Notification notification) {
        return null;
    }
    
}
