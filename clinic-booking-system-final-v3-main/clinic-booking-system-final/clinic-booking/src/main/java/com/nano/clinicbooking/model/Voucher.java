package com.nano.clinicbooking.model;

import com.nano.clinicbooking.enums.VoucherType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    private Double discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type;  // ✅ Loại voucher: SERVICE_TICKET hoặc DISCOUNT

    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean enable = true;

}
