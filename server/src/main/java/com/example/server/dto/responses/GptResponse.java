package com.example.server.dto.responses;

import com.example.server.models.GptChoice;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GptResponse {

    @JsonProperty(required = true)
    private String id;

    @JsonProperty(required = true)
    private String object;

    @JsonProperty(required = true)
    private int created;

    @JsonProperty(required = true)
    private String model;

    @JsonProperty(required = true)
    private GptChoice[] choices;
}
