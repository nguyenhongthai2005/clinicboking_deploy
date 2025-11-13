package com.nano.clinicbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAnalyticsDto {
    private Long doctorId;
    private String fullName;
    private String specialtyName;
    private Long totalAppointments;
}
