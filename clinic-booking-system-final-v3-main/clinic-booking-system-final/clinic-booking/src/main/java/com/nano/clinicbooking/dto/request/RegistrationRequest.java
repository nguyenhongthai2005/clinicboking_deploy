package com.nano.clinicbooking.dto.request;

import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class RegistrationRequest {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String gender;
    private String userType;
    private Boolean isEnable;

    private String specialization;
    private String experience;
    private String degree;
    private String description;


}
