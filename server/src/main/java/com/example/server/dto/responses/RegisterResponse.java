package com.example.server.dto.responses;

import com.example.server.enums.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private boolean status;
    private String id;
    private String username;
    private Role role;
}
