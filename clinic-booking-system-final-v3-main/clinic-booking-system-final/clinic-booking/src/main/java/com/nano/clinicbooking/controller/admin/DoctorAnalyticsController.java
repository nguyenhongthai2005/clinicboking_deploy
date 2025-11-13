package com.nano.clinicbooking.controller.admin;

import com.nano.clinicbooking.dto.response.DoctorAnalyticsDto;
import com.nano.clinicbooking.service.analytics.DoctorAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/analytics/doctors")
public class DoctorAnalyticsController {

    @Autowired
    private DoctorAnalyticsService service;

    @GetMapping("/top")
    public ResponseEntity<List<DoctorAnalyticsDto>> getTopDoctors(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(service.getTopDoctors(limit));
    }
}
