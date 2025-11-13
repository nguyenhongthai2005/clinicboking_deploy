package com.nano.clinicbooking.dto.response;

import com.nano.clinicbooking.enums.SlotStatus;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ShiftSlotDto {
    private Long id;
    private Integer slotNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;
    private AppointmentDto appointment;
}
