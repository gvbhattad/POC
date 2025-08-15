package com.example.excelprocessor.service;

import com.example.excelprocessor.config.AppConfig;
import com.example.excelprocessor.model.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelFileService {

    private final AppConfig appConfig;

    public List<ExcelRow> readExcel(InputStream inputStream) throws IOException {
        List<ExcelRow> rows = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> rowIterator = sheet.iterator();
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        int rowCount = 0;
        while (rowIterator.hasNext() && rowCount < appConfig.getProcessing().getRecordLimit()) {
            Row row = rowIterator.next();
            ExcelRow excelRow = new ExcelRow();

            // Use DataFormatter to handle numeric cells as strings correctly
            excelRow.setAccountNumber(dataFormatter.formatCellValue(row.getCell(0)));
            excelRow.setPhoneNumber(dataFormatter.formatCellValue(row.getCell(1)));
            excelRow.setLineType(dataFormatter.formatCellValue(row.getCell(2)));
            excelRow.setConfirmed(dataFormatter.formatCellValue(row.getCell(3)));
            excelRow.setSsn(dataFormatter.formatCellValue(row.getCell(4)));
            excelRow.setDob(dataFormatter.formatCellValue(row.getCell(5)));
            excelRow.setChannelId(dataFormatter.formatCellValue(row.getCell(6)));

            rows.add(excelRow);
            rowCount++;
        }
        workbook.close();
        return rows;
    }

    public ByteArrayOutputStream writeExcel(List<ExcelRow> rows) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Processing_Report");

        // Header
        String[] headers = {"AccountNumber", "PhoneNumber", "LineType", "Confirmed", "SSN", "DOB", "ChannelID", "Status"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data
        int rowNum = 1;
        for (ExcelRow rowData : rows) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowData.getAccountNumber());
            row.createCell(1).setCellValue(rowData.getPhoneNumber());
            row.createCell(2).setCellValue(rowData.getLineType());
            row.createCell(3).setCellValue(rowData.getConfirmed());
            row.createCell(4).setCellValue(rowData.getSsn());
            row.createCell(5).setCellValue(rowData.getDob());
            row.createCell(6).setCellValue(rowData.getChannelId());
            row.createCell(7).setCellValue(rowData.getStatus());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream;
    }
}