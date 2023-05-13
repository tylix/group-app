package com.maximilianwiegmann.backend.group.data;

import com.mongodb.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class GroupInvite {
    private String token;
    private final long timestamp = System.currentTimeMillis();
    private long expire;
    @Nullable
    private String receiver;

    public boolean isExpired() {
        return System.currentTimeMillis() > expire;
    }
}
