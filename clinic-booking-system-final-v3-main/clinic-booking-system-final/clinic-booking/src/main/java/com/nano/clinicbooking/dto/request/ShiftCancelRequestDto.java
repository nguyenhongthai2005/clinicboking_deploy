package com.nano.clinicbooking.dto.request;

import lombok.Data;

@Data
public class ShiftCancelRequestDto {
    private Long shiftId;
    private String reason;
}
