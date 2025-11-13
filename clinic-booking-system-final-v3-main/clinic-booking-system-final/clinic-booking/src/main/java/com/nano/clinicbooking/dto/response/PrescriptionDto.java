package com.nano.clinicbooking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PrescriptionDto {
    private Long id;
    private String medicineName;
    private String dosage;
    private String duration;
    private String instructions;
}
