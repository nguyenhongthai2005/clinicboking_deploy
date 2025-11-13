package com.nano.clinicbooking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người dùng được cấp voucher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Voucher mẫu
    @ManyToOne(fetch = FetchType.EAGER)  //né lỗi lazy + @readOnly trong service để đẹp cho dữ liệu lớn nhiều
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    // Ngày được cấp
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    // Đánh dấu đã dùng chưa
    @Builder.Default
    @Column(nullable = false)
    private Boolean used = false;
}
