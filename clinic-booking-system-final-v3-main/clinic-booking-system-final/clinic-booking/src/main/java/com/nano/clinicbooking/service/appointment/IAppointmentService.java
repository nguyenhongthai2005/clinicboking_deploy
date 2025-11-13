package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.dto.request.BookAppointmentRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {
    Appointment createAppointment(BookAppointmentRequest request, Long patientId, Long specialtyId, Long doctorId);

    AppointmentDto getAppointmentById(Long id);

    List<AppointmentDto> getAllAppointments();

    AppointmentDto updateAppointment(Long id, Appointment request);

    void deleteAppointment(Long id);


    @Transactional(readOnly = true)
    List<AppointmentDto> getAppointmentsByStatusAndDate(String status, LocalDate date);

    @Transactional(readOnly = true)
    List<AppointmentDto> getAppointmentsByStatus(String status);

    AppointmentDto changeStatus(Long id, AppointmentStatus newStatus, Long actorUserId);

    List<AppointmentDto> getAppointmentsByDoctorAndStatus(Long doctorId, AppointmentStatus status);

    @Transactional(readOnly = true)
    List<AppointmentDto> getAppointmentsByShiftAndStatuses(Long shiftId, List<AppointmentStatus> statuses);

    List<AppointmentDto> getAppointmentsByPatientAndStatuses(Long patientId, List<AppointmentStatus> statuses);

    @Transactional
    AppointmentDto rescheduleAppointment(Long appointmentId, Long newShiftId);

    // 🩺 Lễ tân xem toàn bộ cuộc hẹn trong ngày
    @Transactional(readOnly = true)
    List<AppointmentDto> getAppointmentsByDate(LocalDate date);

    // 👨‍⚕️ Bác sĩ xem lịch hẹn của riêng mình trong ngày
    @Transactional(readOnly = true)
    List<AppointmentDto> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date);

    @Transactional(readOnly = true)
    AppointmentDto getOnlineMeetingInfoForPatient(Long appointmentId, Long patientId);

    @Transactional
    AppointmentDto startOnlineConsultation(Long appointmentId);

}
