package com.nano.clinicbooking.dto.response.groupheath;
import lombok.*;
import java.time.LocalDate;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupAssignmentView {
    private Long assignmentId;
    private Long specialtyId;
    private String specialtyName;
    private Long doctorId;
    private String doctorName;
    private Long shiftId;
    private LocalDate shiftDate;
    private String shiftType;
    private Integer capacityAllocated;
    private String note;
}