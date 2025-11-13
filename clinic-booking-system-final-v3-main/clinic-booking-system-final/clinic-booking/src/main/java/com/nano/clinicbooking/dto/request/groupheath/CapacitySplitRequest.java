package com.nano.clinicbooking.dto.request.groupheath;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CapacitySplitRequest {
    private Integer maxPerFile;                 // mặc định 20
    private List<String> shiftOrder;            // ví dụ: ["MORNING","AFTERNOON","EVENING"]
}
