package com.nano.clinicbooking.dto.response.groupheath;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CapacitySplitResponse {
    private Long requestId;
    private int totalMembers;
    private int maxPerFile;
    private List<SplitPartInfo> parts;
}
