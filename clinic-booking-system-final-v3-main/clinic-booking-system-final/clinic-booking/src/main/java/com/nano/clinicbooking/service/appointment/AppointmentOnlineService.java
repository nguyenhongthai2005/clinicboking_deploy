package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * 💻 Service phụ trách xử lý khám online (Telemedicine)
 * gồm join meeting, start meeting, generate meeting link,...
 */
@Service
@RequiredArgsConstructor
public class AppointmentOnlineService {

    private final AppointmentRepository appointmentRepo;
    private final AppointmentQueryService queryService;
    private final ModelMapper mapper;

    /**
     * 👤 Bệnh nhân lấy thông tin phòng họp Jitsi cho cuộc hẹn online
     */
    @Transactional(readOnly = true)
    public AppointmentDto getOnlineMeetingInfoForPatient(Long appointmentId, Long patientId) {
        Appointment app = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!app.getPatient().getId().equals(patientId)) {
            throw new IllegalStateException("You are not authorized to join this meeting");
        }

        if (app.getType() != AppointmentType.ONLINE) {
            throw new IllegalStateException("This is not an online appointment");
        }

        if (app.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Appointment is not confirmed yet");
        }

        return queryService.getAppointmentById(app.getId());
    }

    /**
     * 👨‍⚕️ Bác sĩ bắt đầu buổi khám online → chuyển trạng thái sang IN_PROGRESS
     */
    @Transactional
    public AppointmentDto startOnlineConsultation(Long appointmentId) {
        Appointment app = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (app.getType() != AppointmentType.ONLINE)
            throw new IllegalStateException("This is not an online consultation");

        if (app.getStatus() != AppointmentStatus.CONFIRMED &&
                app.getStatus() != AppointmentStatus.CHECKED_IN)
            throw new IllegalStateException("Appointment must be confirmed before starting");

        app.setStatus(AppointmentStatus.IN_PROGRESS);
        app.setActualStartTime(LocalTime.now());
        appointmentRepo.save(app);

        return queryService.getAppointmentById(app.getId());
    }
}
