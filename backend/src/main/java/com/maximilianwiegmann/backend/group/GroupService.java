package com.maximilianwiegmann.backend.group;

import com.maximilianwiegmann.backend.group.data.GroupData;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import com.maximilianwiegmann.backend.payload.response.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupData getGroup(String id) {
        return groupRepository.findById(id).orElse(null);
    }

    public GroupResponse createGroup(GroupData groupData) {
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("requireTfa", false);
        groupData.setSettingMap(defaultMap);
        return groupRepository.save(groupData).toResponse();
    }

    public List<GroupResponse> getGroupsByUser(String uid) {
        return groupRepository.findAllByMemberId(uid).stream().map(GroupData::toResponse).toList();
    }

    public List<GroupData> getGroupsDataByUser(String uid) {
        return groupRepository.findAllByMemberId(uid);
    }

    public GroupResponse getGroupById(String gid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return null;
        return group.toResponse();
    }

    public GroupResponse updateGroup(GroupData groupData) {
        GroupData group = groupRepository.findById(groupData.getId()).orElse(null);
        if (group == null)
            return null;
        if (groupData.getName() != null)
            group.setName(groupData.getName());
        if (groupData.getDescription() != null)
            group.setDescription(groupData.getDescription());
        if (groupData.getIcon() != null)
            group.setIcon(groupData.getIcon());
        if (groupData.getMember() != null)
            group.setInvited(groupData.getInvited());
        if (groupData.getRequests() != null)
            group.setRequests(groupData.getRequests());
        if (groupData.getSettingMap() != null)
            group.setSettingMap(groupData.getSettingMap());
        return groupRepository.save(group).toResponse();
    }

    public List<Document> getGroupRequests(String uid) {
        return groupRepository.findAllByInvitedContains(uid).stream().map(data -> new Document()
                .append("gId", data.getId())
                .append("name", data.getName())
                .append("description", data.getDescription())
                .append("icon", data.getIcon())
                .append("member", data.getMember().size())
        ).toList();
    }

    public GroupResponse joinGroup(String gid, String uid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return null;
        if (group.getInvited().stream().noneMatch(invited -> invited.getId().equals(uid)) || group.getMember().stream().anyMatch(member -> member.getId().equals(uid)))
            return null;
        group.getMember().add(new GroupMember(uid, System.currentTimeMillis(), GroupMember.ROLE_MEMBER));
        group.getInvited().removeIf(invited -> invited.getId().equals(uid));
        group.getRequests().removeIf(request -> request.getId().equals(uid));
        return groupRepository.save(group).toResponse();
    }

    public boolean leaveGroup(String gid, String uid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return false;
        if (group.getMember().stream().noneMatch(member -> member.getId().equals(uid)))
            return false;
        group.getMember().removeIf(member -> member.getId().equals(uid));
        groupRepository.save(group);
        return true;
    }

    public boolean inviteUser(String gid, String uid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return false;
        if (group.getMember().stream().noneMatch(member -> member.getId().equals(uid)))
            return false;
        group.getInvited().add(new GroupMember(uid, System.currentTimeMillis(), GroupMember.ROLE_MEMBER));
        return true;
    }

    public boolean deleteChat(String gId) {
        return false;
    }

    public boolean deleteChatFromUser(String gId, String uId) {
        return false;
    }

    public boolean deleteChatById(String gId, String mId) {
        return false;
    }

    public boolean deleteGroup(String gid) {
        if (!groupRepository.existsById(gid))
            return false;
        groupRepository.deleteById(gid);
        return true;
    }

}
