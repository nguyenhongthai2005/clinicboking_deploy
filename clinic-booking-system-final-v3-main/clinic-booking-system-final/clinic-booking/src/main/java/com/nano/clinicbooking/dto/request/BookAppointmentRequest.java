package com.nano.clinicbooking.dto.request;

import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.PatientInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookAppointmentRequest {
    private Appointment appointment;
    private List<PatientInformation> patients;
    private Long shiftId;
}
