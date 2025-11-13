package com.nano.clinicbooking.dto.request.groupheath;
import lombok.*;
import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupApproveRequest {
    private List<GroupAssignmentItem> assignments;
    private String note;
}