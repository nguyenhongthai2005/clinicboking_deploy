package com.nano.clinicbooking.dto.response.groupheath;
import lombok.*;
import java.time.LocalDate;
import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupApprovalSummaryResponse {
    private Long requestId;
    private String status;
    private String groupName;
    private String departments;
    private LocalDate preferredDate;
    private List<GroupAssignmentView> assignments;
}
