package com.maximilianwiegmann.backend.group.data;

import com.maximilianwiegmann.backend.chat.ChatMessage;
import com.maximilianwiegmann.backend.payload.response.GroupResponse;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Document(collection = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GroupData {

    @Id
    private String id;

    private String name;
    private String description;
    private long created;
    private String icon;
    private Map<String, Object> settingMap;
    private List<GroupMember> member;
    private List<GroupInvite> invited;
    private List<GroupMember> requests;
    private List<ChatMessage> messages;

    public GroupResponse toResponse() {
        return GroupResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .icon(icon)
                .created(created)
                .member(member)
                .invited(invited)
                .requests(requests)
                .messages(messages)
                .settingMap(settingMap)
                .build();
    }

    public void updateInvite(GroupInvite invite) {
        invited.removeIf(current -> current.getToken().equals(invite.getToken()));
        invited.add(invite);
    }

}
