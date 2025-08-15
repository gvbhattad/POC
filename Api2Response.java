package com.example.excelprocessor.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Api2Response {
    private String responseCode;
    private String responseDesc;
    private String transId;
    private PhoneDetails phoneDetails;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhoneDetails {
        private boolean confirmed;
        private String lineType;
        private String trustScore;
        private String source;
        private String payfoneIdMatch;
    }
}