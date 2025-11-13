package com.nano.clinicbooking.dto.response.groupheath;

import com.nano.clinicbooking.dto.response.DoctorSuggestionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    GroupHealthAnalysisResponse {
    private Long requestId;
    private String groupName;
    private String departments;
    private LocalDate preferredDate;
    private Integer totalMembers;
    private List<GroupHealthMemberInfo> members;
    private List<DoctorSuggestionDto> suggestedDoctors;
}
