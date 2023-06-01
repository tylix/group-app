package com.maximilianwiegmann.backend.notifications;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query("{ 'uId' : ?0 }")
    List<Notification> findByUserId(String uId);

    @Query("{ 'uId' : ?0, 'read' : false }")
    List<Notification> findByUserIdAndNotRead(String uId);

}
