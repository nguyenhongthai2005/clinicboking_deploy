package com.nano.clinicbooking.controller.shiftCancelController;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.service.doctorShiftCancelService.DoctorShiftCancelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctor/shifts")
@RequiredArgsConstructor
public class DoctorShiftCancelController {

    private final DoctorShiftCancelService cancelService;

    @PostMapping("/{shiftId}/cancel-request")
    public ResponseEntity<?> requestCancel(
            @PathVariable Long shiftId,
            @RequestBody ShiftCancelRequestDto dto,
            @AuthenticationPrincipal User doctor // hoặc từ token
    ) {
        var request = cancelService.createCancelRequest(doctor.getId(), shiftId, dto.getReason());
        return ResponseEntity.ok(request);
    }
}
