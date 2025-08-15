package com.example.excelprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.excelprocessor.config")
public class ExcelProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelProcessorApplication.class, args);
    }

}