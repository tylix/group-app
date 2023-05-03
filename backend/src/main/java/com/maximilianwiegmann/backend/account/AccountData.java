package com.maximilianwiegmann.backend.account;

import com.maximilianwiegmann.backend.security.token.Token;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class AccountData implements UserDetails {

    @Id
    private String id;

    private String username;
    private String firstName;
    private String lastName;

    private String password;
    private String email;

    private String tfaSecret;

    @DBRef(db = "tokens")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
