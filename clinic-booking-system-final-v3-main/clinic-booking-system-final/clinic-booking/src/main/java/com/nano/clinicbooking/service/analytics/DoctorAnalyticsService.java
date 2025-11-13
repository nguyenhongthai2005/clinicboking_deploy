package com.nano.clinicbooking.service.analytics;

import com.nano.clinicbooking.dto.response.DoctorAnalyticsDto;
import com.nano.clinicbooking.repository.analytics.DoctorAnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorAnalyticsService {

    @Autowired
    private DoctorAnalyticsRepository repo;

    public List<DoctorAnalyticsDto> getTopDoctors(int limit) {
        return repo.findTopDoctors(limit).stream()
                .map(row -> new DoctorAnalyticsDto(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Long) row[3]
                ))
                .toList();
    }
}
