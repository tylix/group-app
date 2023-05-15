package com.maximilianwiegmann.backend.group.data;

import com.mongodb.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class GroupInvite {
    private String token;
    @Default
    private long timestamp = System.currentTimeMillis();
    private long expire;
    @Nullable
    private String receiver;

    private int used;
    private int maxUses;

    public boolean isExpired() {
        return (expire != -1 && timestamp + expire < System.currentTimeMillis()) || (maxUses != 0 && used >= maxUses);
    }
}
