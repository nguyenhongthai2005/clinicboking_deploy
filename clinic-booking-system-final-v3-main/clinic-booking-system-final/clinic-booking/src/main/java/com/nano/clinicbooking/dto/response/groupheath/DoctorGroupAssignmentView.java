package com.nano.clinicbooking.dto.response.groupheath;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorGroupAssignmentView {

    private Long assignmentId;       // id của GroupHealthAssignment
    private Long requestId;          // id của GroupHealthRequest
    private String groupName;        // tên nhóm khám sức khỏe

    private Long specialtyId;        // id chuyên khoa
    private String specialtyName;    // tên chuyên khoa (Cardiology, Khoa mắt,...)

    private Long shiftId;            // id ca khám
    private LocalDate shiftDate;     // ngày khám
    private String shiftType;        // MORNING / AFTERNOON / EVENING

    private Integer capacityAllocated; // số người được phân bổ cho bác sĩ này
}
