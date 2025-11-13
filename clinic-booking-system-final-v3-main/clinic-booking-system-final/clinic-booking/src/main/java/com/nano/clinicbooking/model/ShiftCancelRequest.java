package com.nano.clinicbooking.model;

import com.nano.clinicbooking.enums.ShiftCancelStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ShiftCancelRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private DoctorShift shift;

    @Enumerated(EnumType.STRING)
    private ShiftCancelStatus status; // PENDING / APPROVED / REJECTED

    private String reason;

    private String note; // ⬅️ Thêm trường này để ghi chú khi reject

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy; // người duyệt (receptionist)

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
