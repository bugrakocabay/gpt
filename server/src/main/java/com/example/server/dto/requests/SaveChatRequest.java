package com.example.server.dto.requests;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveChatRequest {
    private String id;
    private String userId;
}
