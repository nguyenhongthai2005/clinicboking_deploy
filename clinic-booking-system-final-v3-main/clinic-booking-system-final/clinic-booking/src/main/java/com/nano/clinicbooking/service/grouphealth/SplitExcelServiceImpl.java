package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.response.groupheath.CapacitySplitResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthMemberInfo;
import com.nano.clinicbooking.dto.response.groupheath.SplitPartInfo;
import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import com.nano.clinicbooking.repository.grouphealth.GroupHealthRequestRepository;
import com.nano.clinicbooking.utils.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SplitExcelServiceImpl implements SplitExcelService {

    private final GroupHealthRequestRepository requestRepo;

    @Override
    public CapacitySplitResponse splitAndSuggest(Long requestId, int maxPerFile, List<String> shiftOrder) throws Exception {
        GroupHealthRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // 1) Đọc members từ file gốc
        var members = ExcelParser.parseMembers(req.getExcelFilePath());
        int total = members.size();
        if (total == 0) throw new IllegalStateException("Excel has no members");

        // 🔹 1.1. Lấy danh sách tên chuyên khoa từ request.departments
        List<String> specialtyNames = Arrays.stream(
                        Optional.ofNullable(req.getDepartments()).orElse("")
                                .split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // 2) Tạo thư mục output
        Path baseDir = Paths.get(req.getExcelFilePath()).getParent();
        if (baseDir == null) baseDir = Paths.get("uploads/group-health/" + requestId);
        Files.createDirectories(baseDir);

        // 3) Tách thành các parts kích thước maxPerFile
        List<SplitPartInfo> parts = new ArrayList<>();
        int idx = 0;
        int shiftIdx = 0;

        for (int start = 0; start < total; start += maxPerFile) {
            int end = Math.min(start + maxPerFile, total);
            var sub = members.subList(start, end);
            idx++;

            String suggestedShift = shiftOrder.get(Math.min(shiftIdx, shiftOrder.size() - 1));
            shiftIdx++; // mỗi part gợi ý sang ca tiếp theo

            // 4) Ghi part ra file Excel mới
            String filename = "request-" + requestId + "-part-" + idx + ".xlsx";
            Path out = baseDir.resolve(filename);

            // 🆕 truyền thêm specialtyNames để tạo cột Result / Note
            writePartExcel(sub, out.toString(), specialtyNames);

            parts.add(SplitPartInfo.builder()
                    .index(idx)
                    .size(sub.size())
                    .filePath(out.toString())
                    .suggestedShift(suggestedShift)
                    .build());
        }

        return CapacitySplitResponse.builder()
                .requestId(requestId)
                .totalMembers(total)
                .maxPerFile(maxPerFile)
                .parts(parts)
                .build();
    }

    // 🆕 Tạo file .xlsx với:
    // Name | Email | Phone | Result - Cardiology | Note - Cardiology | Result - Khoa mắt | Note - Khoa mắt | ...
    private void writePartExcel(List<GroupHealthMemberInfo> items,
                                String outputPath,
                                List<String> specialtyNames) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Members");
            int r = 0;

            // header
            Row h = sheet.createRow(r++);
            int col = 0;
            h.createCell(col++).setCellValue("Name");
            h.createCell(col++).setCellValue("Email");
            h.createCell(col++).setCellValue("Phone");

            // 🔹 mỗi chuyên khoa: 2 cột Result + Note
            if (specialtyNames != null) {
                for (String s : specialtyNames) {
                    h.createCell(col++).setCellValue("Result - " + s);
                    h.createCell(col++).setCellValue("Note - " + s);
                }
            }

            // rows
            for (var m : items) {
                Row row = sheet.createRow(r++);
                col = 0;

                row.createCell(col++).setCellValue(Objects.toString(m.getName(), ""));
                row.createCell(col++).setCellValue(Objects.toString(m.getEmail(), ""));
                row.createCell(col++).setCellValue(Objects.toString(m.getPhone(), ""));

                // Cột Result/Note để trống – bác sĩ điền sau
                if (specialtyNames != null) {
                    for (int i = 0; i < specialtyNames.size(); i++) {
                        row.createCell(col++).setBlank(); // Result
                        row.createCell(col++).setBlank(); // Note
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                wb.write(fos);
            }
        }
    }
}
