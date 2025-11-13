package com.nano.clinicbooking.controller.event;

import com.nano.clinicbooking.dto.response.EventResponse;
import com.nano.clinicbooking.dto.response.UserEventResponse;
import com.nano.clinicbooking.service.ev_vc.EventService;
import com.nano.clinicbooking.service.user.UserEventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class UserEventController {

    private final EventService eventService;
    private final UserEventService userEventService;

    // ✅ 1. Xem tất cả event khả dụng (ai cũng xem được)
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllAvailableEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // ✅ 2. User đăng ký tham gia event
    @PreAuthorize("hasAuthority('Patient') or hasAuthority('User')")
    @PostMapping("/{eventId}/register")
    public ResponseEntity<UserEventResponse> registerEvent(@PathVariable Long eventId, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        UserEventResponse response = userEventService.registerUserForEvent(userId, eventId);
        return ResponseEntity.ok(response);
    }
}
