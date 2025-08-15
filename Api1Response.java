package com.example.excelprocessor.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Api1Response {
    private String transId;
    private String eventName;
    private ResponsePayload response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponsePayload {
        private String code;
        private String flag;
        private String message;
        private String operation;
    }
}