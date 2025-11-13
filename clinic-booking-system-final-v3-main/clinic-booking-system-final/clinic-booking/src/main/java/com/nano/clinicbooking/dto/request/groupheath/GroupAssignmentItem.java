package com.nano.clinicbooking.dto.request.groupheath;
import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupAssignmentItem {
    private Long specialtyId;
    private Long doctorId;
    private Long shiftId;
    private Integer capacityAllocated;
}