package com.example.server.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDto {

    private String id;
    private String message;
    private boolean status;

    public ChatResponseDto() {
    }

    public ChatResponseDto(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public ChatResponseDto(String id, String message, boolean status) {
        this.id = id;
        this.message = message;
        this.status = status;
    }
}
