package com.nano.clinicbooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nano.clinicbooking.enums.VoucherType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherRequest {

    private String code;
    private String name;
    private Double discountValue;

    private VoucherType type;  // ✅ Loại voucher (SERVICE_TICKET hoặc DISCOUNT)

    private Boolean enable = true;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime expiryDate;
}
