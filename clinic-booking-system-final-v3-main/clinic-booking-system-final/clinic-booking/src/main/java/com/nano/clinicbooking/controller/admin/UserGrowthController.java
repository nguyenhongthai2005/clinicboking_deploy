package com.nano.clinicbooking.controller.admin;

import com.nano.clinicbooking.dto.response.UserGrowthStatsDto;
import com.nano.clinicbooking.service.analytics.UserGrowthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGrowthController {

    @Autowired
    private UserGrowthService service;

    @GetMapping("/api/admin/analytics/users/growth")
    public UserGrowthStatsDto getUserGrowth() {
        return service.getUserGrowthStats();
    }
}
