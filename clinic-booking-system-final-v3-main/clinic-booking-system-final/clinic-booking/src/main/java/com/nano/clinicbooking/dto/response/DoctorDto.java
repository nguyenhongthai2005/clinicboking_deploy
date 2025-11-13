package com.nano.clinicbooking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String degree;
    private String description;
    private String experience;
    private Long specialtyId;
    private String specialtyName;
}

