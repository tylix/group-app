package com.maximilianwiegmann.backend.notifications;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByAccountDataId(String accountDataId);
    
}
