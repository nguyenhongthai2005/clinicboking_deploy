package com.nano.clinicbooking.dto.response.groupheath;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SplitPartInfo {
    private int index;               // 1..n
    private int size;                // số người trong file này
    private String filePath;         // đường dẫn lưu file part
    private String suggestedShift;   // gợi ý "MORNING"/"AFTERNOON"/"EVENING"
}
