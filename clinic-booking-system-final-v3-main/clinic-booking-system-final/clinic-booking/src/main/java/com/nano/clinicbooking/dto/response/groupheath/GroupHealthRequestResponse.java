package com.nano.clinicbooking.dto.response.groupheath;

import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupHealthRequestResponse {
    private Long id;
    private String groupName;
    private String phoneNumber;
    private String departments;
    private LocalDate preferredDate;
    private String excelFilePath;
    private GroupHealthRequest.RequestStatus status;
}
