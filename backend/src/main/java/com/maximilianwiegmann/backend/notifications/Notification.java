package com.maximilianwiegmann.backend.notifications;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.maximilianwiegmann.backend.account.AccountData;

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
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String title;
    private String body;
    private long timestamp;
    private String link;
    @Default
    private boolean read = false;

    @DBRef(lazy = true)
    @JsonBackReference
    private AccountData accountData;
}
