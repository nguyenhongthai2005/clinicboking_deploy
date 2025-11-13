package com.nano.clinicbooking.controller.groupheath;


import com.nano.clinicbooking.dto.request.groupheath.CapacitySplitRequest;
import com.nano.clinicbooking.dto.request.groupheath.GroupApproveRequest;
import com.nano.clinicbooking.dto.response.groupheath.CapacitySplitResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupApprovalSummaryResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthSummaryResponse;
import com.nano.clinicbooking.service.grouphealth.GroupHealthApprovalService;
import com.nano.clinicbooking.service.grouphealth.SplitExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/group-health")
@RequiredArgsConstructor
public class GroupHealthAdminController {


    private final GroupHealthApprovalService approvalService;
    private final SplitExcelService splitService;

    @GetMapping
    public ResponseEntity<List<GroupHealthSummaryResponse>> getAllRequests(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<GroupHealthSummaryResponse> list = approvalService.getAllRequests(status, page, size);
        return ResponseEntity.ok(list);
    }


    @PostMapping("/{id}/approve")
    public ResponseEntity<GroupApprovalSummaryResponse> approve(
            @PathVariable Long id,
            @RequestBody GroupApproveRequest body) {
        return ResponseEntity.ok(approvalService.approve(id, body));
    }

    @PostMapping("/{id}/split")
    public ResponseEntity<CapacitySplitResponse> splitExcel(
            @PathVariable Long id,
            @RequestBody CapacitySplitRequest body
    ) throws Exception {
        int cap = (body.getMaxPerFile() != null && body.getMaxPerFile() > 0) ? body.getMaxPerFile() : 20;
        List<String> order = (body.getShiftOrder() != null && !body.getShiftOrder().isEmpty())
                ? body.getShiftOrder()
                : List.of("MORNING", "AFTERNOON", "EVENING");

        CapacitySplitResponse res = splitService.splitAndSuggest(id, cap, order);
        return ResponseEntity.ok(res);
    }

    // 🆕 Reject request
    @PostMapping("/{id}/reject")
    public ResponseEntity<GroupApprovalSummaryResponse> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "No reason provided") : "No reason provided";
        return ResponseEntity.ok(approvalService.reject(id, reason));
    }

    // ✅ Admin duyệt kết quả & đánh dấu COMPLETED
    @PostMapping("/{id}/approve-results")
    public ResponseEntity<GroupApprovalSummaryResponse> approveResults(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(approvalService.approveResultsAndSendToCustomer(id));
    }


}