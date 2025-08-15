package com.example.excelprocessor.model;

import lombok.Data;

@Data
public class ExcelRow {
    private String accountNumber;
    private String phoneNumber;
    private String lineType;
    private String confirmed;
    private String ssn;
    private String dob;
    private String channelId;
    
    // Fields for processing
    private String transId;
    private String status;
}