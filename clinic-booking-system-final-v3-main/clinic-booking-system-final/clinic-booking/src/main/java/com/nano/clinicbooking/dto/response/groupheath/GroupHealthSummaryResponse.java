package com.nano.clinicbooking.dto.response.groupheath;

import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupHealthSummaryResponse {
    private Long id;
    private String groupName;
    private String phoneNumber;
    private LocalDate preferredDate;
    private String status;
    private LocalDateTime createdAt;
}
