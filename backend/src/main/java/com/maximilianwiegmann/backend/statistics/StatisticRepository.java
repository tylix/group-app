package com.maximilianwiegmann.backend.statistics;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;

public interface StatisticRepository<T> {
    
    @Query(" {'?2' : { $gte: ?0, $lte: ?1 } }")
    List<T> findByFromTo(long from, long to, String timeName);

}
