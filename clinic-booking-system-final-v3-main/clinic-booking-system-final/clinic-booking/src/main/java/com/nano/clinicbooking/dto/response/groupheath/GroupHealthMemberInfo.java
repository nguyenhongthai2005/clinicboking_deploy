package com.nano.clinicbooking.dto.response.groupheath;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupHealthMemberInfo {
    private String name;
    private String email;
    private String phone;
}
