package com.example.excelprocessor.model.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Api1Request {
    private String transId;
    private String eventName;
    private String eventTimestamp;
    private String serverName;
    private String action;
    private RequestMetadata requestMetadata;
    private RequestPayload request;

    @Data
    @Builder
    public static class RequestMetadata {
        private Version version;
    }

    @Data
    @Builder
    public static class Version {
        private String major;
        private String minor;
        private String patch;
        private String versionDate;
    }

    @Data
    @Builder
    public static class RequestPayload {
        private String accountnumber;
        private String payfoneid;
        private String phonenumber;
        private String linetype;
        private String carrier;
        private String countrycode;
        private String verified;
        private String namescore;
        private String addressscore;
        private String eventtype;
        private String ssn;
        private String dob;
        private String lexid;
        private String syfid;
        private String tokenid;
        private String clientid;
        private String sys;
        private String prin;
        private String agent;
        private String loaddatetime;
        private String details;
        private String eventdate;
        private String eventadditionalInfo;
        private String reasoncodes;
        private String lastcalldate;
        private String statusflag;
        private String acct_type;
        private String cif;
        private String phnsource;
        private String firstloaddate;
        private String velocity;
        private String firstauthdate;
        private String contactibility;
        private String acctopendate;
        private String email;
        private String emailsource;
        private String emailloaddate;
        private String reubenrefreshdate;
    }
}