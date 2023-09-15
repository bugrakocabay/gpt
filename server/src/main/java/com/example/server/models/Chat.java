package com.example.server.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class Chat {

    private String _id;
    private String conversationId;
    private Message[] message;
    private String userId;
    private String alias;
}
