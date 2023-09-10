package com.example.server.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "chat")
public class Chat {

    private String _id;
    private String conversationId;
    private Message[] message;

    public Chat() {
    }

    public Chat(String _id, String conversationId, Message[] message) {
        this._id = _id;
        this.conversationId = conversationId;
        this.message = message;
    }
}
