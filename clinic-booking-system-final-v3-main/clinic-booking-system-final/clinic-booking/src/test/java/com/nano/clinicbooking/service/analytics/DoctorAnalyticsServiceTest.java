package com.nano.clinicbooking.service.analytics;


import com.nano.clinicbooking.dto.response.DoctorAnalyticsDto;
import com.nano.clinicbooking.repository.analytics.DoctorAnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorAnalyticsServiceTest {

    @Mock
    private DoctorAnalyticsRepository repo;

    @InjectMocks
    private DoctorAnalyticsService service;

    @Test
    void testGetTopDoctors() {
        // GIVEN
        List<Object[]> mockResult = List.of(
                new Object[]{1L, "Dr. Alice", "Cardiology", 10L},
                new Object[]{2L, "Dr. Bob", "Dermatology", 8L}
        );
        when(repo.findTopDoctors(5)).thenReturn(mockResult);

        // WHEN
        List<DoctorAnalyticsDto> result = service.getTopDoctors(5);

        // THEN
        assertEquals(2, result.size());
        assertEquals("Dr. Alice", result.get(0).getFullName());
        assertEquals(10L, result.get(0).getTotalAppointments());
        verify(repo, times(1)).findTopDoctors(5);
    }
}
