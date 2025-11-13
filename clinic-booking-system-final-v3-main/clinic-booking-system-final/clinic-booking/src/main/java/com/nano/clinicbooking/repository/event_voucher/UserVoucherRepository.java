package com.nano.clinicbooking.repository.event_voucher;

import com.nano.clinicbooking.model.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    // 📋 Lấy tất cả voucher của user
    List<UserVoucher> findByUserId(Long userId);

    // 📋 Tìm 1 voucher cụ thể theo id và user (để dùng trong API /use)
    Optional<UserVoucher> findByIdAndUserId(Long id, Long userId);

    // ✅ Kiểm tra user đã được cấp voucher này chưa
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
}
