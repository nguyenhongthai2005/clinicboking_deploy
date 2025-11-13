package com.nano.clinicbooking.repository.analytics;

import com.nano.clinicbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 📊 Repository phục vụ thống kê tăng trưởng người dùng.
 */
@Repository
public interface UserGrowthRepository extends JpaRepository<User, Long> {

    /**
     * 🔹 Đếm số user mới đăng ký trong 7 ngày gần nhất.
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM users 
        WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)
    long countNewUsersThisWeek();

    /**
     * 🔹 Đếm số user đăng ký trong khoảng từ 7–14 ngày trước.
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM users 
        WHERE created_at >= DATE_SUB(NOW(), INTERVAL 14 DAY)
          AND created_at < DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)
    long countNewUsersLastWeek();
}
