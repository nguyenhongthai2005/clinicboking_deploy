package com.nano.clinicbooking.dto.response.doctorshedule;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DoctorWeeklyNamesResponse {
    private Long specialtyId;
    private String specialtyName;
    private LocalDate weekStart; // Thứ 2
    private LocalDate weekEnd;   // Chủ nhật
    private List<DayNamesDTO> days;
}
