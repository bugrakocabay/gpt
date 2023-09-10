package com.example.server.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message {

    private String message;
    private String response;

    public Message() {
    }

    public Message(String message, String response) {
        this.message = message;
        this.response = response;
    }
}
