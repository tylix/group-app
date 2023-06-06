package com.maximilianwiegmann.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicAccountResponse {
    private String uId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private int status;
}
