package com.nano.clinicbooking.controller.groupheath;

import com.nano.clinicbooking.dto.response.groupheath.DoctorGroupAssignmentView;
import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import com.nano.clinicbooking.repository.grouphealth.GroupHealthRequestRepository;
import com.nano.clinicbooking.service.grouphealth.DoctorGroupHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor/group-health")
@RequiredArgsConstructor
public class DoctorGroupHealthController {

    private final DoctorGroupHealthService doctorGroupHealthService;
    private final GroupHealthRequestRepository groupHealthRequestRepository;

    // 🔹 1) Doctor xem các nhóm được phân công trong ngày
    @GetMapping("/assignments")
    public List<DoctorGroupAssignmentView> getAssignments(
            @RequestParam Long doctorId,               // TODO: sau này lấy từ token
            @RequestParam(required = false) String date
    ) {
        LocalDate d = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        return doctorGroupHealthService.getAssignmentsForDoctor(doctorId, d);
    }

    // 🔹 2) Doctor tải file Excel part (đã split)
    //    Quy ước tên file: request-{requestId}-part-{partIndex}.xlsx
    @GetMapping("/requests/{requestId}/parts/{partIndex}/download")
    public ResponseEntity<Resource> downloadPart(
            @PathVariable Long requestId,
            @PathVariable int partIndex
    ) throws Exception {

        GroupHealthRequest req = groupHealthRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Path baseDir = Paths.get(req.getExcelFilePath()).getParent();
        if (baseDir == null) {
            throw new IllegalStateException("Base directory not found for excelFilePath: " + req.getExcelFilePath());
        }

        String filename = "request-" + requestId + "-part-" + partIndex + ".xlsx";
        Path filePath = baseDir.resolve(filename);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Part file not found: " + filePath);
        }

        byte[] bytes = Files.readAllBytes(filePath);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PostMapping("/requests/{requestId}/parts/{partIndex}/upload")
    public ResponseEntity<String> uploadResult(
            @PathVariable Long requestId,
            @PathVariable int partIndex,
            @RequestParam Long doctorId,          // TODO: sau này lấy từ token
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String savedPath = doctorGroupHealthService
                .uploadResultFile(requestId, partIndex, doctorId, file);

        return ResponseEntity.ok("Uploaded result to: " + savedPath);
    }

}
