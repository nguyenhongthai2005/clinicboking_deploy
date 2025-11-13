package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.mapper.AppointmentMapper;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentQueryService {

    private final AppointmentRepository appointmentRepo;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public AppointmentDto getAppointmentById(Long id) {
        Appointment app = appointmentRepo.findWithAllById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        return appointmentMapper.toDto(app);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepo.findAll().stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByStatusAndDate(String status, LocalDate date) {
        AppointmentStatus s = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepo.findByStatusAndAppointmentDate(s, date)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByStatus(String status) {
        AppointmentStatus s = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRepo.findByStatus(s)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentRepo.findByDoctorIdAndAppointmentDate(doctorId, date)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status) {
        return appointmentRepo.findByDoctorIdAndStatus(doctorId, status)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByShiftAndStatuses(Long shiftId, List<AppointmentStatus> statuses) {
        return appointmentRepo.findByShiftIdAndStatusIn(shiftId, statuses)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByPatientAndStatuses(Long patientId, List<AppointmentStatus> statuses) {
        return appointmentRepo.findByPatientIdAndStatusIn(patientId, statuses)
                .stream().map(appointmentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByDate(LocalDate date) {
        return appointmentRepo.findByAppointmentDate(date)
                .stream().map(appointmentMapper::toDto).toList();
    }
}
