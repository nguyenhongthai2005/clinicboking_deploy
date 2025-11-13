package com.nano.clinicbooking.dto.response;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;


    @Transient
    private String specialization;
    private String degree;
    private String description;
    private String experience;

}
