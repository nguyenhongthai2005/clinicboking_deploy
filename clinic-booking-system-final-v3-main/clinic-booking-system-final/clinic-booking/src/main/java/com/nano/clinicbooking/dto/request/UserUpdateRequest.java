package com.nano.clinicbooking.dto.request;

import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String fullName;
    private String phoneNumber;
    private String gender;

    private String specialization;
    private String description;
    private String experience;
    private String degree;

    // Thêm field specialtyId để update specialty của doctor
    private Long specialtyId;

}
