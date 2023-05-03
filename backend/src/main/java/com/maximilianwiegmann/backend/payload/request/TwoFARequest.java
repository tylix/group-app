package com.maximilianwiegmann.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TwoFARequest {
    private final String token, code;
}
