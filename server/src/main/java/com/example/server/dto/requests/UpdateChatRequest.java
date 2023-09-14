package com.example.server.dto.requests;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatRequest {

    private String id;
    private String message;
}
