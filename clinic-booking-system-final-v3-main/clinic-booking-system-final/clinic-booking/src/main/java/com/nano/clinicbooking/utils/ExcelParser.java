package com.nano.clinicbooking.utils;

import com.nano.clinicbooking.dto.response.groupheath.GroupHealthMemberInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;     // .xls
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;     // .xlsx

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ExcelParser {

    public static List<GroupHealthMemberInfo> parseMembers(String filePath) throws IOException {
        Path p = Paths.get(filePath);
        if (!Files.exists(p) || Files.size(p) == 0) {
            throw new IllegalArgumentException("Excel file not found or empty: " + filePath);
        }

        // đọc toàn bộ bytes -> dùng lại nhiều lần cho từng branch
        byte[] data = Files.readAllBytes(p);
        String lower = filePath.toLowerCase();

        // 1) .xlsx? (zip signature 'PK')
        if (looksZip(data)) {
            try (InputStream in = new ByteArrayInputStream(data);
                 Workbook wb = new XSSFWorkbook(in)) {
                return readSheetToMembers(wb.getSheetAt(0));
            } catch (Exception ignore) {
                // fallthrough
            }
        }

        // 2) .xls?
        if (lower.endsWith(".xls")) {
            try (InputStream in = new ByteArrayInputStream(data);
                 Workbook wb = new HSSFWorkbook(in)) {
                return readSheetToMembers(wb.getSheetAt(0));
            } catch (Exception ignore) {
                // fallthrough
            }
        }

        // 3) CSV fallback (kể cả khi đuôi không phải .csv)
        return parseCsvMembers(data);
    }

    private static boolean looksZip(byte[] data) {
        return data != null && data.length >= 2 && data[0] == 'P' && data[1] == 'K';
    }

    private static List<GroupHealthMemberInfo> readSheetToMembers(Sheet sheet) {
        List<GroupHealthMemberInfo> out = new ArrayList<>();
        if (sheet == null) return out;

        DataFormatter fmt = new DataFormatter();
        boolean headerDetected = false;

        int rowIdx = 0;
        for (Row row : sheet) {
            String c0 = getCellString(row, 0, fmt);
            String c1 = getCellString(row, 1, fmt);
            String c2 = getCellString(row, 2, fmt);

            if (rowIdx == 0) {
                headerDetected = looksLikeHeader(c0, c1, c2);
                if (headerDetected) { rowIdx++; continue; }
            }

            if (isBlank(c0) && isBlank(c1) && isBlank(c2)) { rowIdx++; continue; }
            out.add(new GroupHealthMemberInfo(nullToEmpty(c0), nullToEmpty(c1), nullToEmpty(c2)));
            rowIdx++;
        }
        return out;
    }

    private static String getCellString(Row row, int idx, DataFormatter fmt) {
        if (row == null) return null;
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        String s = fmt.formatCellValue(cell);
        return s != null ? s.trim() : null;
    }

    private static boolean looksLikeHeader(String c0, String c1, String c2) {
        return contains(c0, "name") || contains(c1, "email") || contains(c2, "phone");
    }

    private static boolean contains(String s, String needle) {
        return s != null && s.toLowerCase().contains(needle);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static List<GroupHealthMemberInfo> parseCsvMembers(byte[] data) throws IOException {
        List<GroupHealthMemberInfo> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                String[] arr = splitCsvLine(line);
                String c0 = arr.length > 0 ? arr[0].trim() : "";
                String c1 = arr.length > 1 ? arr[1].trim() : "";
                String c2 = arr.length > 2 ? arr[2].trim() : "";

                if (first) {
                    first = false;
                    if (looksLikeHeader(c0, c1, c2)) continue;
                }
                if (isBlank(c0) && isBlank(c1) && isBlank(c2)) continue;

                out.add(new GroupHealthMemberInfo(c0, c1, c2));
            }
        }
        return out;
    }

    // tách CSV cơ bản, hỗ trợ dấu phẩy trong dấu "
    private static String[] splitCsvLine(String line) {
        if (line == null) return new String[0];
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"'); i++;
                } else {
                    inQuote = !inQuote;
                }
            } else if (ch == ',' && !inQuote) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
