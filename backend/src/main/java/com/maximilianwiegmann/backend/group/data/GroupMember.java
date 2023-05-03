package com.maximilianwiegmann.backend.group.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
@Builder
public class GroupMember {
    private String id;
    private long timestamp;
    private int role;

    public static int ROLE_OWNER = 0;
    public static int ROLE_MEMBER = 1;
}
