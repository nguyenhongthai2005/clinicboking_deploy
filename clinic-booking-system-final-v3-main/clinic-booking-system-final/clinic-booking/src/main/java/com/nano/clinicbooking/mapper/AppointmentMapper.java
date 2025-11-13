package com.nano.clinicbooking.mapper;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.dto.response.PatientInfoDto;
import com.nano.clinicbooking.dto.response.PrescriptionDto;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.PatientInformation;
import com.nano.clinicbooking.model.Prescription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    public AppointmentDto toDto(Appointment app) {
        if (app == null) return null;

        AppointmentDto dto = new AppointmentDto();
        dto.setId(app.getId());
        dto.setReason(app.getReason());
        dto.setAppointmentDate(app.getAppointmentDate());
        dto.setAppointmentTime(app.getAppointmentTime());
        dto.setAppointmentNo(app.getAppointmentNo());
        if (app.getCreatedAt() != null)
            dto.setCreatedAt(app.getCreatedAt().toLocalDate());
        dto.setStatus(app.getStatus());
        dto.setType(app.getType());
        dto.setMeetingUrl(app.getMeetingUrl());
        dto.setJoinCode(app.getJoinCode());

        if (app.getPatient() != null)
            dto.setPatientName(app.getPatient().getFullName());
        if (app.getDoctor() != null)
            dto.setDoctorName(app.getDoctor().getFullName());
        if (app.getSpecialty() != null)
            dto.setSpecialtyName(app.getSpecialty().getName());

        // ✅ Map bệnh nhân phụ
        if (app.getPatientInfos() != null && !app.getPatientInfos().isEmpty()) {
            dto.setPatients(app.getPatientInfos().stream()
                    .map(this::mapPatient)
                    .collect(Collectors.toList()));
        }

        // ✅ Map đơn thuốc
        if (app.getPrescriptions() != null && !app.getPrescriptions().isEmpty()) {
            dto.setPrescriptions(app.getPrescriptions().stream()
                    .map(this::mapPrescription)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private PatientInfoDto mapPatient(PatientInformation p) {
        PatientInfoDto dto = new PatientInfoDto();
        dto.setId(p.getId());
        dto.setFullName(p.getFullName());
        dto.setGender(p.getGender());
        dto.setPhoneNumber(p.getPhoneNumber());
        dto.setAddress(p.getAddress());
        dto.setDob(p.getDob());
        dto.setRelationship(p.getRelationship());
        return dto;
    }

    private PrescriptionDto mapPrescription(Prescription pre) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(pre.getId());
        dto.setMedicineName(pre.getMedicineName());
        dto.setDosage(pre.getDosage());
        dto.setInstructions(pre.getInstructions());
        return dto;
    }
}


