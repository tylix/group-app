package com.maximilianwiegmann.backend.group;

import com.maximilianwiegmann.backend.BackendApplication;
import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.account.AccountRepository;
import com.maximilianwiegmann.backend.chat.ChatMessage;
import com.maximilianwiegmann.backend.group.data.GroupData;
import com.maximilianwiegmann.backend.group.data.GroupInvite;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import com.maximilianwiegmann.backend.notifications.Notification;
import com.maximilianwiegmann.backend.notifications.NotificationHandler;
import com.maximilianwiegmann.backend.payload.response.GroupResponse;
import com.mongodb.lang.Nullable;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationHandler notificationHandler;

    public GroupData getGroup(String id) {
        return groupRepository.findById(id).orElse(null);
    }

    public List<GroupData> getGroups(long from, long to) {
        return groupRepository.findByFromTo(from, to, "created");
    }

    public List<ChatMessage> getMessages(long from, long to) {
        List<GroupData> groups = groupRepository.findByFromTo(from, to, "created");
        return groups.stream().map(GroupData::getMessages).flatMap(List::stream).toList();
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
                .append("member", data.getMember().size())).toList();
    }

    public GroupResponse joinGroup(String gid, String uid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return null;
        if (group.getInvited().stream()
                .noneMatch(invited -> invited.getReceiver() != null && invited.getReceiver().equals(uid))
                || group.getMember().stream().anyMatch(member -> member.getId().equals(uid)))
            return null;
        group.getMember().add(new GroupMember(uid, System.currentTimeMillis(), GroupMember.ROLE_MEMBER));
        group.getInvited().removeIf(invited -> invited.getReceiver() != null && invited.getReceiver().equals(uid));
        group.getRequests().removeIf(request -> request.getId().equals(uid));
        return groupRepository.save(group).toResponse();
    }

    public GroupResponse joinByLink(String token, AccountData account) {
        GroupData group = groupRepository.findByInviteToken(token).stream().findFirst().orElse(null);
        if (group == null)
            return null;
        if (group.getMember().stream().anyMatch(member -> member.getId().equals(account.getId())))
            return null;
        GroupInvite invite = group.getInvited().stream().filter(invited -> invited.getToken().equals(token)).findFirst()
                .orElse(null);
        if (invite == null)
            return null;
        if (invite.isExpired())
            return null;
        if ((Boolean) group.getSettingMap().get("requireTfa") && account.getTfaSecret() == null)
            return null;
        group.getMember().add(new GroupMember(account.getId(), System.currentTimeMillis(), GroupMember.ROLE_MEMBER));
        invite.use();
        group.updateInvite(invite);

        if (invite.getIssuer() != null)
            notificationHandler.notifyUser(invite.getIssuer(),
                    Notification.builder()
                            .title("Invite code has been used")
                            .body(account.getUsername() + " has used your invite link.")
                            .build());
        return groupRepository.save(group).toResponse();
    }

    public GroupResponse leaveGroup(String gid, String uid) {
        GroupData group = groupRepository.findById(gid).orElse(null);
        if (group == null)
            return null;
        if (group.getMember().stream().noneMatch(member -> member.getId().equals(uid)))
            return null;
        group.getMember().removeIf(member -> member.getId().equals(uid));
        if (group.getMember().isEmpty()) {
            deleteGroup(group.getId());
            return null;
        }
        group = groupRepository.save(group);
        return group.toResponse();
    }

    public ResponseEntity<?> createInviteLink(String gId, long expire, @Nullable String reciever, int maxUses,
            String issuer) {
        GroupData groupData = getGroup(gId);
        if (groupData == null)
            return ResponseEntity.badRequest().build();
        if (groupData.getInvited().stream()
                .anyMatch(invite -> invite.getReceiver() != null && invite.getReceiver().equals(reciever)
                        && !invite.isExpired()))
            return ResponseEntity.badRequest().build();

        String token = BackendApplication.generateString(20);

        GroupInvite invite = GroupInvite.builder().token(token).expire(expire == 0 ? -1 : expire).maxUses(maxUses)
                .receiver(reciever).issuer(issuer)
                .build();
        groupData.getInvited().add(invite);
        groupRepository.save(groupData);

        if (reciever != null) {
            notificationHandler.notifyUser(reciever,
                    Notification.builder()
                            .title("Group Invite")
                            .body("You have been invited to " + groupData.getName())
                            .link("https://maximilianwiegmann.com/groups?invlink=" + token)
                            .build());
        }

        return ResponseEntity.ok(invite);
    }

    public GroupData getGroupByInviteToken(String token) {
        GroupData group = groupRepository.findByInviteToken(token).stream().findFirst().orElse(null);
        if (group == null)
            return null;
        GroupInvite invite = group.getInvited().stream().filter(invited -> invited.getToken().equals(token)).findFirst()
                .orElse(null);
        if (invite == null)
            return null;
        if (invite.isExpired())
            return null;
        AccountData account = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (invite.getReceiver() != null && !invite.getReceiver().equals(account.getId()))
            return null;
        return group;
        // return groupRepository.findAll().stream().filter(groupData ->
        // groupData.getInvited().stream() .anyMatch(invite -> invite.getToken() != null
        // && invite.getToken().equals(token) &&
        // !invite.isExpired())).findFirst().orElse(null);
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
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.DELETE);

        messagingTemplate.convertAndSend(String.format("/channel/%s", gid), chatMessage);
        groupRepository.deleteById(gid);
        return true;
    }

}
