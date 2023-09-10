package com.example.server.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateChatDto {

    private String id;
    private String message;

    public UpdateChatDto() {
    }

    public UpdateChatDto(String id, String message) {
        this.id = id;
        this.message = message;
    }

}
