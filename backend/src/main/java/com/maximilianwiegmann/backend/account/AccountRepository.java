package com.maximilianwiegmann.backend.account;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.retry.stats.StatisticsRepository;

import com.maximilianwiegmann.backend.statistics.StatisticRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface AccountRepository extends MongoRepository<AccountData, String>, StatisticRepository<AccountData> {

    Stream<AccountData> findAllBy();

    Optional<AccountData> findByUsername(String username);

    Optional<AccountData> findByEmail(String email);

    @Query("{ $or: [ { 'username': { $regex: '?0', $options: 'i' } }, { 'firstName': { $regex: '?0', $options: 'i' } } ] }")
    Stream<AccountData> findAllByUsernameContains(String username);

    @Query("{ 'contactRequests' : { $elemMatch: { 'targetUId' : ?0 } } }")
    List<AccountData> findByContactRequest(String uId);

}
