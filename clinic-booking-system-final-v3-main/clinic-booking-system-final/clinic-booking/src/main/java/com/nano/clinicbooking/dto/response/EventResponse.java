package com.nano.clinicbooking.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isEnabled;
    // thông tin voucher kèm theo (nếu có)
    private Long voucherId;
    private String voucherCode;
    private String voucherName;
}
