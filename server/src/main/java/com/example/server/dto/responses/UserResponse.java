package com.example.server.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

        private String id;
        private String username;
        private String role;
        private String apiKey;
        private String orgId;
}
