package com.maximilianwiegmann.backend.authentication;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AuthStatus {

    private String token;
    private Status status;
    private String uId;
    private long deviceId;
    private int mobileConfirm;
    private int fakeOne;
    private int fakeTwo;
    private boolean rememberMe;
    private boolean mobile;
    private boolean otp;

    public enum Status {
        OK,
        WAIT,
        ERROR
    }
}
