package com.maximilianwiegmann.backend.notifications;

import org.json.JSONObject;
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
    @Default
    private long timestamp = System.currentTimeMillis();
    private String link;
    @Default
    private boolean read = false;

    private String uId;

    public String toJson() {
        return new JSONObject().put("id", id).put("title", title).put("body", body).put("timestamp", timestamp)
                .put("link", link).put("read", read).toString();
    }
}
