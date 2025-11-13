package com.nano.clinicbooking.controller.recep;

import com.nano.clinicbooking.dto.response.UserEventResponse;
import com.nano.clinicbooking.service.receptionist.ReceptionistEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reception/events")
@RequiredArgsConstructor
public class ReceptionistEventController {

    private final ReceptionistEventService receptionistEventService;

    // ✅ Receptionist xác nhận người dùng đến tham gia event
    @PreAuthorize("hasAuthority('Receptionist')")
    @PostMapping("/{eventId}/confirm/{userId}")
    public ResponseEntity<UserEventResponse> confirmAttendance(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        UserEventResponse response = receptionistEventService.confirmUserAttendance(eventId, userId);
        return ResponseEntity.ok(response);
    }
}
