package com.example.excelprocessor.config;

import com.example.excelprocessor.model.ProcessingMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    private ProcessingConfig processing;
    private ApiConfig api1;
    private ApiConfig api2;
    private ApiDefaults apiDefaults;

    @Getter
    @Setter
    public static class ProcessingConfig {
        private ProcessingMode mode;
        private int recordLimit;
        private long waitTimeMs;
    }

    @Getter
    @Setter
    public static class ApiConfig {
        private String url;
    }

    @Getter
    @Setter
    public static class ApiDefaults {
        private Api1Defaults api1;
        private Api2Defaults api2;
    }

    @Getter
    @Setter
    public static class Api1Defaults {
        private String eventName;
        private String serverName;
        private String action;
        private Version version;
        private String carrier;
        private String countryCode;
        private String nameScore;
        private String addressScore;
        private String eventType;
        private String defaultSsn;
        private String defaultDob;
        private String lexId;
        private String syfId;
        private String tokenId;
        private String clientId;
        private String sys;
        private String agent;

        @Getter
        @Setter
        public static class Version {
            private String major;
            private String minor;
            private String patch;
        }
    }

    @Getter
    @Setter
    public static class Api2Defaults {
        private String clientId;
        private String channelId;
        private String consentType;
        private String defaultSsn;
        private String defaultDob;
    }
}