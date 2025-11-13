package com.nano.clinicbooking.controller.shiftCancelController;

import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.service.doctorShiftCancelService.ShiftCancelApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receptionist/cancel-requests")
@RequiredArgsConstructor
public class ShiftCancelApprovalController {

    private final ShiftCancelApprovalService approvalService;

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @AuthenticationPrincipal User receptionist) {
        return ResponseEntity.ok(approvalService.approveRequest(id, receptionist.getId()));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                    @AuthenticationPrincipal User receptionist,
                                    @RequestParam String note) {
        return ResponseEntity.ok(approvalService.rejectRequest(id, receptionist.getId(), note));
    }
}
