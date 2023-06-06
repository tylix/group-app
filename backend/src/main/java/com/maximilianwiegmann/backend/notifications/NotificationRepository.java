package com.maximilianwiegmann.backend.notifications;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.maximilianwiegmann.backend.statistics.StatisticRepository;

public interface NotificationRepository extends MongoRepository<Notification, String>, StatisticRepository<Notification> {

    @Query("{ 'uId' : ?0 }")
    List<Notification> findByUserId(String uId);

    @Query("{ 'uId' : ?0, 'read' : false }")
    List<Notification> findByUserIdAndNotRead(String uId);

}
