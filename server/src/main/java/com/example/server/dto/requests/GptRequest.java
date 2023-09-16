package com.example.server.dto.requests;

import com.example.server.models.GptMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GptRequest {

    @JsonProperty(required = true)
    private String model;

    @JsonProperty(required = true)
    private float temperature;

    @JsonProperty(required = true)
    private List<GptMessage> messages;
}
