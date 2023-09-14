package com.example.server.dto.responses;

import com.example.server.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private boolean status;
    private String id;
    private String username;
    private Role role;
    private String token;
}
