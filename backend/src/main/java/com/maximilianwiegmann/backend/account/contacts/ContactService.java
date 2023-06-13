package com.maximilianwiegmann.backend.account.contacts;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.maximilianwiegmann.backend.BackendApplication;
import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.account.AccountRepository;
import com.maximilianwiegmann.backend.account.AccountService;
import com.maximilianwiegmann.backend.notifications.Notification;
import com.maximilianwiegmann.backend.notifications.NotificationHandler;
import com.maximilianwiegmann.backend.payload.response.PublicAccountResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final AccountRepository accountRepository;
    private final NotificationHandler notificationHandler;

    public List<ContactData> getContacts(AccountData account) {
        return account.getContacts();
    }

    public List<ContactData> getRequests(AccountData account) {
        return account.getContactRequests();
    }

    public List<PublicAccountResponse> getRequested(AccountData account) {
        return accountRepository.findByContactRequest(account.getId()).stream()
                .map(data -> PublicAccountResponse.builder()
                        .username(data.getUsername()).firstName(data.getFirstName()).lastName(data.getLastName())
                        .avatar(data.getAvatar()).status(notificationHandler.getStatus(data.getId())).build())
                .toList();
    }

    public ContactData sendRequest(AccountData account, String targetUId) {
        AccountData targetAccount = accountRepository.findById(targetUId).orElse(null);
        if (targetAccount == null)
            return null;
        if (account.getContacts().stream().anyMatch(contact -> contact.getTargetUId().equals(targetUId)))
            return null;

        if (targetAccount.getContactRequests().stream()
                .anyMatch(request -> request.getTargetUId().equals(account.getId())))
            return null;
        if (account.getContactRequests().stream().anyMatch(request -> request.getTargetUId().equals(targetUId)))
            return acceptRequest(account, targetUId);

        ContactData request = ContactData.builder().id(BackendApplication.generateString(20)).targetUId(account.getId())
                .build();
        targetAccount.getContactRequests().add(request);
        accountRepository.save(targetAccount);

        notificationHandler.notifyUser(targetUId, Notification.builder()
                .title("You received a friend request")
                .body(account.getUsername() + " want's to be your friend.")
                .link("https://maximilianwiegmann.com/contacts?request=" + request.getId()).build());
        return request;
    }

    public ContactData acceptRequest(AccountData account, String targetUId) {
        AccountData targetAccount = accountRepository.findById(targetUId).orElse(null);
        if (targetAccount == null)
            return null;
        account.getContactRequests().removeIf(request -> request.getTargetUId().equals(targetUId));

        ContactData newContact = ContactData.builder().targetUId(targetUId).build();
        account.getContacts().add(newContact);
        targetAccount.getContacts().add(ContactData.builder().targetUId(account.getId()).build());

        accountRepository.save(account);
        accountRepository.save(targetAccount);

        notificationHandler.notifyUser(targetUId, Notification.builder()
                .title("Friend request accepted")
                .body(account.getUsername() + " has accepted your request.").build());

        return newContact;
    }

    public boolean denyRequest(AccountData account, String targetUId) {
        AccountData targetAccount = accountRepository.findById(targetUId).orElse(null);
        if (targetAccount == null)
            return false;

        if (account.getContactRequests().stream().noneMatch(contact -> contact.getTargetUId().equals(targetUId)))
            return false;

        account.getContactRequests().removeIf(contact -> contact.getTargetUId().equals(targetUId));
        accountRepository.save(account);
        return true;
    }

    public boolean removeContact(AccountData account, String targetUId) {
        AccountData targetAccount = accountRepository.findById(targetUId).orElse(null);
        if (targetAccount == null)
            return false;

        if (account.getContacts().stream().noneMatch(contact -> contact.getTargetUId().equals(targetUId)))
            return false;

        account.getContacts().removeIf(contact -> contact.getTargetUId().equals(targetUId));
        targetAccount.getContacts().removeIf(contact -> contact.getTargetUId().equals(account.getId()));
        accountRepository.save(account);
        accountRepository.save(targetAccount);
        return true;
    }

}
