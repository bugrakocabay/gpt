package com.example.server.chat;

import lombok.Getter;

@Getter
public class ChatDto {
    private String id;

    public ChatDto() {
    }

    public ChatDto(String id) {
        this.id = id;
    }
}
