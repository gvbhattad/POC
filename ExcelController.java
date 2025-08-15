package com.example.excelprocessor.controller;

import com.example.excelprocessor.model.ExcelRow;
import com.example.excelprocessor.service.ExcelFileService;
import com.example.excelprocessor.service.ExcelProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelFileService excelFileService;
    private final ExcelProcessingService excelProcessingService;

    @PostMapping("/process")
    public ResponseEntity<Resource> processExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 1. Read data from the uploaded Excel file
            List<ExcelRow> rowsToProcess = excelFileService.readExcel(file.getInputStream());

            // 2. Process the data (call APIs, etc.)
            List<ExcelRow> processedRows = excelProcessingService.processRows(rowsToProcess);

            // 3. Write the processed data to a new in-memory Excel file
            ByteArrayOutputStream outputStream = excelFileService.writeExcel(processedRows);

            // 4. Prepare the response for download
            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processed_data.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (IOException e) {
            // In a real app, you'd have better error handling
            return ResponseEntity.status(500).build();
        }
    }
}