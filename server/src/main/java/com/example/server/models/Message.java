package com.example.server.models;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String message;
    private String response;
}
