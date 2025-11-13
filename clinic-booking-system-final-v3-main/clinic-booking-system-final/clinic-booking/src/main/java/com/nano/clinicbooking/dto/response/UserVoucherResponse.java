package com.nano.clinicbooking.dto.response;

import com.nano.clinicbooking.enums.VoucherType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucherResponse {
    private Long id;
    private Long voucherId;
    private String voucherCode;
    private String voucherName;
    private Double discountValue;
    private VoucherType type;
    private LocalDateTime expiryDate;
    private LocalDateTime issuedAt;
    private Boolean used;
}
