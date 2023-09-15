package com.example.server.dto.requests;

import com.example.server.models.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChatRequest {

        private String conversationId;
        private String alias;
}