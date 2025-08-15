package com.example.excelprocessor.model.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Api2Request {
    private Body body;
    private Header header;

    @Data
    @Builder
    public static class Body {
        private String account;
        private String phoneNumber;
        private String address;
        private String city;
        private String clientId;
        private String extendedAddress;
        private String firstName;
        private String lastName;
        private String ssn;
        private String dateOfBirth;
        private String state;
        private String zipCode;
        private String payfoneId;
    }

    @Data
    @Builder
    public static class Header {
        private String channelId;
        private String consentType;
        private String version;
        private String transId;
    }
}