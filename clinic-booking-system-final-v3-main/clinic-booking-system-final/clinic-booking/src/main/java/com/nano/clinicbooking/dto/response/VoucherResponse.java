package com.nano.clinicbooking.dto.response;

import com.nano.clinicbooking.enums.VoucherType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {

    private Long id;
    private String code;
    private String name;
    private Double discountValue;
    private VoucherType type;     // ✅ hiển thị loại voucher
    private Boolean enable;
    private LocalDateTime expiryDate;
}
