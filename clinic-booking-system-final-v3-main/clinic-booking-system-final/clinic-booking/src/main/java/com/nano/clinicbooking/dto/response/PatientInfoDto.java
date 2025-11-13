

package com.nano.clinicbooking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientInfoDto {
    private Long id;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String address;
    private String dob;
    private String relationship;
}

