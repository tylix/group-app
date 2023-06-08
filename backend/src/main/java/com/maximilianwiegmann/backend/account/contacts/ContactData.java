package com.maximilianwiegmann.backend.account.contacts;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactData {
    
    private String id;
    private String targetUId;
    @Default
    private long timestamp = System.currentTimeMillis();

}
