package com.nano.clinicbooking.dto.response.doctorshedule;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftNamesDTO {
    private String shift;         // "MORNING" | "AFTERNOON" | "EVENING"
    private List<String> doctors; // chỉ tên bác sĩ
}
