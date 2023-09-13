package com.example.server.chat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private String id;
    private String userId;
}
