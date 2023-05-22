package com.maximilianwiegmann.backend.account;

import com.maximilianwiegmann.backend.payload.response.PublicAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public ResponseEntity<PublicAccountResponse> getAccountData() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(accountData == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(service.findByUid(accountData.getId()));
    }

    @GetMapping("/{uId}")
    public ResponseEntity<PublicAccountResponse> getAccountData(@PathVariable String uId) {
        return ResponseEntity.ok(service.findByUid(uId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<PublicAccountResponse> getAccountDataByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getUserByUsername(username));
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<List<PublicAccountResponse>> searchAccountDataByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.searchByUsername(username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PublicAccountResponse>> getAllAccountData() {
        return ResponseEntity.ok(service.findAll());
    }

}
