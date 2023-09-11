package com.example.server.user;

import lombok.*;
import org.springframework.lang.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
