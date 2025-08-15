package com.example.excelprocessor.service;

import com.example.excelprocessor.client.ApiClient;
import com.example.excelprocessor.config.AppConfig;
import com.example.excelprocessor.model.ExcelRow;
import com.example.excelprocessor.model.api.Api1Request;
import com.example.excelprocessor.model.api.Api1Response;
import com.example.excelprocessor.model.api.Api2Request;
import com.example.excelprocessor.model.api.Api2Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelProcessingService {

    private final AppConfig appConfig;
    private final ApiClient apiClient;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIMESTAMP_MS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public List<ExcelRow> processRows(List<ExcelRow> rows) {
        log.info("Starting processing in {} mode.", appConfig.getProcessing().getMode());
        switch (appConfig.getProcessing().getMode()) {
            case SEQUENTIAL:
                return processSequentially(rows);
            case BATCH:
                return processInBatch(rows);
            default:
                throw new IllegalStateException("Unsupported processing mode: " + appConfig.getProcessing().getMode());
        }
    }

    private List<ExcelRow> processSequentially(List<ExcelRow> rows) {
        for (ExcelRow row : rows) {
            String transId = UUID.randomUUID().toString();
            row.setTransId(transId);

            // Step 1: Call API 1
            boolean api1Success = callApi1ForRow(row);

            // Step 2: If API 1 succeeded, wait and call API 2
            if (api1Success) {
                waitFor(appConfig.getProcessing().getWaitTimeMs());
                callApi2ForRow(row);
            }
        }
        return rows;
    }

    private List<ExcelRow> processInBatch(List<ExcelRow> rows) {
        List<ExcelRow> successfulApi1Rows = new ArrayList<>();

        // Phase 1: Call API 1 for all records
        log.info("Batch Mode: Starting API 1 calls for {} records.", rows.size());
        for (ExcelRow row : rows) {
            String transId = UUID.randomUUID().toString();
            row.setTransId(transId);
            if (callApi1ForRow(row)) {
                successfulApi1Rows.add(row);
            }
        }
        log.info("Batch Mode: Completed API 1 calls. {} succeeded.", successfulApi1Rows.size());

        // Phase 2: Wait
        log.info("Batch Mode: Waiting for {} ms.", appConfig.getProcessing().getWaitTimeMs());
        waitFor(appConfig.getProcessing().getWaitTimeMs());

        // Phase 3: Call API 2 for all successful records
        log.info("Batch Mode: Starting API 2 calls for {} records.", successfulApi1Rows.size());
        for (ExcelRow row : successfulApi1Rows) {
            callApi2ForRow(row);
        }
        log.info("Batch Mode: Completed API 2 calls.");

        return rows;
    }

    private boolean callApi1ForRow(ExcelRow row) {
        Api1Request request = buildApi1Request(row);
        Optional<Api1Response> responseOpt = apiClient.callApi1(request);

        if (responseOpt.isPresent()) {
            Api1Response response = responseOpt.get();
            if ("EDLMYSQLUP000".equals(response.getResponse().getCode()) && "Success".equalsIgnoreCase(response.getResponse().getMessage())) {
                log.info("API 1 success for transId: {}", row.getTransId());
                return true;
            }
        }
        log.warn("API 1 failure for transId: {}", row.getTransId());
        row.setStatus("Failure at insert stage");
        return false;
    }

    private void callApi2ForRow(ExcelRow row) {
        Api2Request request = buildApi2Request(row);
        Optional<Api2Response> responseOpt = apiClient.callApi2(request);

        if (responseOpt.isPresent()) {
            Api2Response response = responseOpt.get();
            if (response.getPhoneDetails() != null && response.getPhoneDetails().isConfirmed()) {
                log.info("API 2 success for transId: {}", row.getTransId());
                row.setStatus("Success");
                return;
            }
        }
        log.warn("API 2 failure for transId: {}", row.getTransId());
        row.setStatus("Failure at Phone Confirm Api Stage");
    }

    private void waitFor(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted during wait period.", e);
        }
    }
    
    // --- Request Builder Methods ---

    private Api1Request buildApi1Request(ExcelRow row) {
        AppConfig.Api1Defaults defaults = appConfig.getApiDefaults().getApi1();
        LocalDateTime now = LocalDateTime.now();
        
        Api1Request.Version version = Api1Request.Version.builder()
                .major(defaults.getVersion().getMajor())
                .minor(defaults.getVersion().getMinor())
                .patch(defaults.getVersion().getPatch())
                .versionDate(now.format(TIMESTAMP_FORMATTER))
                .build();

        Api1Request.RequestMetadata metadata = Api1Request.RequestMetadata.builder()
                .version(version)
                .build();

        Api1Request.RequestPayload payload = Api1Request.RequestPayload.builder()
                .accountnumber(row.getAccountNumber())
                .payfoneid(row.getPhoneNumber())
                .phonenumber(row.getPhoneNumber())
                .linetype(StringUtils.hasText(row.getLineType()) ? row.getLineType() : "Mobile")
                .carrier(defaults.getCarrier())
                .countrycode(defaults.getCountryCode())
                .verified(StringUtils.hasText(row.getConfirmed()) ? row.getConfirmed().toUpperCase() : "TRUE")
                .namescore(defaults.getNameScore())
                .addressscore(defaults.getAddressScore())
                .eventtype(defaults.getEventType())
                .ssn(StringUtils.hasText(row.getSsn()) ? row.getSsn() : defaults.getDefaultSsn())
                .dob(StringUtils.hasText(row.getDob()) ? row.getDob() : defaults.getDefaultDob())
                .lexid(defaults.getLexId())
                .syfid(defaults.getSyfId())
                .tokenid(defaults.getTokenId())
                .clientid(defaults.getClientId())
                .sys(defaults.getSys())
                .agent(defaults.getAgent())
                .loaddatetime(now.format(TIMESTAMP_MS_FORMATTER))
                .firstloaddate(LocalDate.now().format(DATE_FORMATTER))
                // Defaulting empty fields as per JSON example
                .prin("1000") 
                .details("")
                .eventdate("")
                .eventadditionalInfo("")
                .reasoncodes("AC|NA|P9")
                .lastcalldate("")
                .statusflag("")
                .acct_type("PLCC")
                .cif("")
                .phnsource("")
                .velocity("")
                .firstauthdate("")
                .contactibility("")
                .acctopendate("")
                .email("")
                .emailsource("")
                .emailloaddate("")
                .reubenrefreshdate("")
                .build();

        return Api1Request.builder()
                .transId(row.getTransId())
                .eventName(defaults.getEventName())
                .eventTimestamp(now.format(TIMESTAMP_FORMATTER))
                .serverName(defaults.getServerName())
                .action(defaults.getAction())
                .requestMetadata(metadata)
                .request(payload)
                .build();
    }

    private Api2Request buildApi2Request(ExcelRow row) {
        AppConfig.Api2Defaults defaults = appConfig.getApiDefaults().getApi2();

        Api2Request.Body body = Api2Request.Body.builder()
                .account(row.getAccountNumber())
                .phoneNumber(row.getPhoneNumber())
                .payfoneId(row.getPhoneNumber())
                .clientId(defaults.getClientId())
                .ssn(StringUtils.hasText(row.getSsn()) ? row.getSsn() : defaults.getDefaultSsn())
                .dateOfBirth(StringUtils.hasText(row.getDob()) ? row.getDob() : defaults.getDefaultDob())
                // Defaulting empty fields as per JSON example
                .address("")
                .city("")
                .extendedAddress("")
                .firstName("")
                .lastName("")
                .state("")
                .zipCode("")
                .build();

        Api2Request.Header header = Api2Request.Header.builder()
                .transId(row.getTransId())
                .channelId(defaults.getChannelId())
                .consentType(defaults.getConsentType())
                .version("")
                .build();

        return Api2Request.builder()
                .body(body)
                .header(header)
                .build();
    }
}