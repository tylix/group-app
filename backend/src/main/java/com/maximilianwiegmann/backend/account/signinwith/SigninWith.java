package com.maximilianwiegmann.backend.account.signinwith;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.maximilianwiegmann.backend.account.AccountData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "signinwith")
public class SigninWith {

    @Id
    private String id;

    private String provider;
    private String providerId;

    @DBRef(lazy = true)
    @JsonBackReference
    private AccountData accountData;
    
}
