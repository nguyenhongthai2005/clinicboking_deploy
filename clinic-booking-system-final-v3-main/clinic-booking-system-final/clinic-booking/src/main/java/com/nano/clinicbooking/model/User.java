package com.nano.clinicbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ✅ Lớp cơ sở cho tất cả loại người dùng:
 * Admin, Doctor, Patient, Receptionist
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) // mỗi subclass có bảng riêng
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;
    private String gender;

    @Column(nullable = false)
    private Boolean isEnable = true;  // dùng để khoá/mở tài khoản

    @Column(nullable = false)
    private String userType; // "Admin", "Doctor", "Patient", "Receptionist"

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Các field sau chỉ được sử dụng cho Doctor (các subclass khác không dùng)
    @Transient
    private String specialization; // tên chuyên khoa (ví dụ: "Tim mạch", "Nội tiết")
    @Transient
    private String description;
    @Transient
    private String experience;
    @Transient
    private String degree;
}
