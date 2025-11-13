package com.nano.clinicbooking.controller.groupheath;

import com.nano.clinicbooking.dto.request.groupheath.GroupHealthRequestUploadRequest;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthAnalysisResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthRequestResponse;
import com.nano.clinicbooking.service.grouphealth.GroupHealthAnalysisService;
import com.nano.clinicbooking.service.grouphealth.GroupHealthRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/group-health")
@RequiredArgsConstructor
public class GroupHealthController {

    private final GroupHealthRequestService groupHealthService;

    /**
     * Upload group health check request (Phase 1)
     * Example form-data:
     *  - groupName: "FPT Software Team"
     *  - phoneNumber: "0901234567"
     *  - specialtyIds: 1,2,3  (hoặc nhiều param specialtyIds=1&specialtyIds=2)
     *  - preferredDate: 2025-11-15
     *  - excelFile: <file>
     */
    @PostMapping("/upload")
    public ResponseEntity<GroupHealthRequestResponse> uploadGroupHealth(
            @RequestParam String groupName,
            @RequestParam String phoneNumber,
            @RequestParam List<Long> specialtyIds,
            @RequestParam(required = false) String preferredDate,
            @RequestParam MultipartFile excelFile
    ) throws IOException {

        GroupHealthRequestUploadRequest request = new GroupHealthRequestUploadRequest();
        request.setGroupName(groupName);
        request.setPhoneNumber(phoneNumber);
        request.setSpecialtyIds(specialtyIds);
        request.setPreferredDate(preferredDate != null ? LocalDate.parse(preferredDate) : null);
        request.setExcelFile(excelFile);

        GroupHealthRequestResponse response = groupHealthService.uploadGroupHealthRequest(request);
        return ResponseEntity.ok(response);
    }

    private final GroupHealthAnalysisService analysisService;

    @GetMapping("/{id}/analyze")
    public GroupHealthAnalysisResponse analyzeRequest(@PathVariable Long id) throws Exception {
        return analysisService.analyze(id);
    }
}
