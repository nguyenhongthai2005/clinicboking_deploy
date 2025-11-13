package com.nano.clinicbooking.service.analytics;

import com.nano.clinicbooking.dto.response.UserGrowthStatsDto;
import com.nano.clinicbooking.listener.SiteVisitListener;
import com.nano.clinicbooking.repository.analytics.UserGrowthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserGrowthServiceTest {

    @Mock
    private UserGrowthRepository userGrowthRepository;

    @InjectMocks
    private UserGrowthService userGrowthService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserGrowthStats() {
        // 🧩 Giả lập dữ liệu mock từ repository
        when(userGrowthRepository.countNewUsersThisWeek()).thenReturn(10L);
        when(userGrowthRepository.countNewUsersLastWeek()).thenReturn(7L);

        // 🔸 Giả lập số session đang hoạt động
        long expectedActiveSessions = SiteVisitListener.getActiveSessions(); // giả định runtime value

        // 🧠 Gọi hàm thực tế
        UserGrowthStatsDto stats = userGrowthService.getUserGrowthStats();

        // ✅ Kiểm tra dữ liệu cơ bản
        assertEquals(10L, stats.getNewUsersThisWeek());
        assertEquals(7L, stats.getNewUsersLastWeek());
        assertEquals(expectedActiveSessions, stats.getTotalVisitsThisWeek());
        assertEquals(Math.max(expectedActiveSessions - 5, 0), stats.getTotalVisitsLastWeek());

        // ✅ Kiểm tra growth rate hợp lý
        if (stats.getTotalVisitsLastWeek() > 0) {
            double expectedGrowthRate = ((double) (stats.getTotalVisitsThisWeek() - stats.getTotalVisitsLastWeek())
                    / stats.getTotalVisitsLastWeek()) * 100;
            assertEquals(expectedGrowthRate, stats.getGrowthPercentage(), 0.001);
        }
    }
}
