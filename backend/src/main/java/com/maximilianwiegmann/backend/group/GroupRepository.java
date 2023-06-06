package com.maximilianwiegmann.backend.group;

import com.maximilianwiegmann.backend.group.data.GroupData;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import com.maximilianwiegmann.backend.statistics.StatisticRepository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<GroupData, String>, StatisticRepository<GroupData> {

    List<GroupData> findAllByMemberId(String uid);

    @Query("{ 'invited' : { $elemMatch: { 'reciever' : ?0 } } }")
    List<GroupData> findAllByInvitedContains(String uid);

    @Query("{ 'invited' : { $elemMatch: { 'token' : ?0 } } }")
    Optional<GroupData> findByInviteToken(String token);

}
