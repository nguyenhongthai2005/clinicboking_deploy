package com.nano.clinicbooking.dto.response.doctorshedule;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DayNamesDTO {
    private LocalDate date;
    private String weekday;              // T2..CN
    private List<ShiftNamesDTO> shifts;  // MORNING/AFTERNOON/EVENING
}
