package com.nano.clinicbooking.dto.response;

import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDto {
    private Long id;
    private String reason;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String appointmentNo;
    private LocalDate createdAt;
    private AppointmentStatus status;

    private String patientName;
    private String doctorName;
    private Long doctorId; 
    private String specialtyName;

    private String meetingUrl;
    private String joinCode;
    private AppointmentType type;

    private List<PatientInfoDto> patients;

    private List<PrescriptionDto> prescriptions;

}
