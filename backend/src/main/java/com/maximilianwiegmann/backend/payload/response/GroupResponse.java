package com.maximilianwiegmann.backend.payload.response;

import com.maximilianwiegmann.backend.chat.ChatMessage;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private String name;
    private String id;
    private String description;
    private String icon;
    private long created;
    private List<GroupMember> member;
    private List<GroupMember> invited;
    private List<GroupMember> requests;
    private List<ChatMessage> messages;
    private Map<String, Object> settingMap;
}
