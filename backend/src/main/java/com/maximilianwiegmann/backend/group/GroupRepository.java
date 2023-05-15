package com.maximilianwiegmann.backend.group;

import com.maximilianwiegmann.backend.group.data.GroupData;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<GroupData, String> {

    List<GroupData> findAllByMemberId(String uid);

    @Query("{ 'invited' : { $elemMatch: { 'reciever' : ?0 } } }")
    List<GroupData> findAllByInvitedContains(String uid);

}
