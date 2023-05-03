package com.maximilianwiegmann.backend.chat;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessage {

    private MessageType type;
    private String content;
    private String userId;
    private long timestamp;
    private String id;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
