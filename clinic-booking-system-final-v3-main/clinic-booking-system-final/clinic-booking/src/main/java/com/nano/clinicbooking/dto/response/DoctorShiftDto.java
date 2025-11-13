package com.nano.clinicbooking.dto.response;

import com.nano.clinicbooking.enums.ShiftType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter
public class DoctorShiftDto {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String specialtyName;
    private LocalDate date;
    private ShiftType shift;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxPatients;
    private String note;

    private List<ShiftSlotDto> slots;

}

