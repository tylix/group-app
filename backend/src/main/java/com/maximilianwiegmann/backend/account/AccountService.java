package com.maximilianwiegmann.backend.account;

import com.maximilianwiegmann.backend.payload.response.PublicAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    public List<PublicAccountResponse> findAll() {
        return repository.findAllBy().map(account -> PublicAccountResponse.builder()
                .uId(account.getId())
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .avatar(null)
                .build()).toList();
    }

    public List<PublicAccountResponse> searchByUsername(String username) {
        return repository.findAllByUsernameContains(username).map(account -> PublicAccountResponse.builder()
                .uId(account.getId())
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .avatar(null)
                .build()).toList();
    }

    public PublicAccountResponse findByUid(String uId) {
        return repository.findById(uId).map(account -> PublicAccountResponse.builder()
                .uId(account.getId())
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .avatar(null)
                .build()).orElse(null);
    }

    public AccountData findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public void setTfaSecret(AccountData account, String tfaSecret) {
        account.setTfaSecret(tfaSecret);
        repository.save(account);
    }

    public PublicAccountResponse getUserByUsername(String username) {
        return repository.findByUsername(username).map(account -> PublicAccountResponse.builder()
                .uId(account.getId())
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .avatar(null)
                .build()).orElse(null);
    }
}
