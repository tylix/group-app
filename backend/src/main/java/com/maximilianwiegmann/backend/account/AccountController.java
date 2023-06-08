package com.maximilianwiegmann.backend.account;

import com.maximilianwiegmann.backend.account.contacts.ContactData;
import com.maximilianwiegmann.backend.account.contacts.ContactService;
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
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<PublicAccountResponse> getAccountData() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
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

    @GetMapping("/status/{id}")
    public ResponseEntity<Integer> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(service.getStatus(id));
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(contactService.getContacts(accountData));
    }

    @PostMapping("/contacts/request/{uId}")
    public ResponseEntity<?> sendRequest(@PathVariable String uId) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(contactService.sendRequest(accountData, uId));
    }

    @PostMapping("/contacts/accept/{uId}")
    public ResponseEntity<?> acceptRequest(@PathVariable String uId) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(contactService.acceptRequest(accountData, uId));
    }

    @GetMapping("/contacts/request/{rId}")
    public ResponseEntity<?> getRequest(@PathVariable String rId) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();

        ContactData request = accountData.getContactRequests().stream().filter(req -> req.getId().equals(rId))
                .findFirst().orElse(null);
        if (request == null)
            return ResponseEntity.status(403).build();

        return ResponseEntity.ok(request);
    }

    @GetMapping("/contacts/requests")
    public ResponseEntity<?> getRequests() {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(contactService.getRequests(accountData));
    }

    @PostMapping("/contacts/remove/{uId}")
    public ResponseEntity<?> removeContact(@PathVariable String uId) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        boolean remove = contactService.removeContact(accountData, uId);
        if (!remove)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(remove);
    }

    @PostMapping("/contacts/deny/{uId}")
    public ResponseEntity<?> denyRequest(@PathVariable String uId) {
        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null)
            return ResponseEntity.status(403).build();
        boolean remove = contactService.denyRequest(accountData, uId);
        if (!remove)
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(remove);
    }
}
