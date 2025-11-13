package com.nano.clinicbooking.service.analytics;

import com.nano.clinicbooking.dto.response.UserGrowthStatsDto;
import com.nano.clinicbooking.listener.SiteVisitListener;
import com.nano.clinicbooking.repository.analytics.UserGrowthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 🧠 Service xử lý logic thống kê tăng trưởng người dùng & traffic site.
 */
@Service
public class UserGrowthService {

    @Autowired
    private UserGrowthRepository userGrowthRepository;

    /**
     * 📊 Lấy dữ liệu thống kê tổng hợp:
     * - User mới theo tuần
     * - Lượt truy cập site (đếm session)
     * - Tăng trưởng traffic tuần này so với tuần trước
     */
    public UserGrowthStatsDto getUserGrowthStats() {
        long newUsersThisWeek = userGrowthRepository.countNewUsersThisWeek();
        long newUsersLastWeek = userGrowthRepository.countNewUsersLastWeek();

        // 🔸 Lấy số session đang hoạt động từ Listener
        long visitsThisWeek = SiteVisitListener.getActiveSessions();
        long visitsLastWeek = Math.max(visitsThisWeek - 5, 0); // Giả định dữ liệu tuần trước tạm thời

        double growthRate = 0.0;
        if (visitsLastWeek > 0) {
            growthRate = ((double) (visitsThisWeek - visitsLastWeek) / visitsLastWeek) * 100;
        }

        return new UserGrowthStatsDto(
                newUsersThisWeek,
                newUsersLastWeek,
                visitsThisWeek,
                visitsLastWeek,
                growthRate
        );
    }
}
