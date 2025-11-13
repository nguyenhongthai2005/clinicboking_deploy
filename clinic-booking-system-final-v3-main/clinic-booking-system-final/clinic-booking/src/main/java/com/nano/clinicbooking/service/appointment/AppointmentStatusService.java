package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.enums.SlotStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.mapper.AppointmentMapper;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.ShiftSlot;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.shiftSlot.ShiftSlotRepository;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.repository.prescription.PrescriptionRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentStatusService {

    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;
    private final ShiftSlotRepository slotRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentDto changeStatus(Long appointmentId, AppointmentStatus newStatus, Long actorUserId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (newStatus == null)
            throw new IllegalStateException("Invalid status");

        switch (newStatus) {
            case CONFIRMED -> handleConfirm(appointment, actorUserId);
            case CHECKED_IN -> handleCheckIn(appointment);
            case IN_PROGRESS -> handleInProgress(appointment);
            case COMPLETED -> handleComplete(appointment);
            case CANCELLED -> handleCancel(appointment);
            case RESCHEDULED -> appointment.setStatus(AppointmentStatus.RESCHEDULED);
            default -> throw new IllegalStateException("Unsupported status");
        }

        appointmentRepo.save(appointment);
        return appointmentMapper.toDto(appointment);
    }

    private void handleConfirm(Appointment appointment, Long actorUserId) {
        if (appointment.getStatus() != AppointmentStatus.PENDING_CONFIRMATION)
            throw new IllegalStateException("Only pending appointments can be confirmed");

        if (actorUserId != null) {
            User actor = userRepo.findById(actorUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));
            appointment.setConfirmedBy(actor);
        }

        if (appointment.getType() == AppointmentType.ONLINE && appointment.getMeetingUrl() == null) {
            String code = UUID.randomUUID().toString().substring(0, 8);
            appointment.setJoinCode(code);
            appointment.setMeetingUrl("https://meet.jit.si/clinic-" + code);
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
    }

    private void handleCheckIn(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED)
            throw new IllegalStateException("Only confirmed appointments can check in");
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointment.setCheckedIn(true);
    }

    private void handleInProgress(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CHECKED_IN)
            throw new IllegalStateException("Only checked-in appointments can start");
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointment.setActualStartTime(LocalTime.now());
    }

    private void handleComplete(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS)
            throw new IllegalStateException("Only appointments in progress can be completed");

        boolean hasPrescription = prescriptionRepo.existsByAppointmentId(appointment.getId());
        if (!hasPrescription)
            throw new IllegalStateException("Appointment must have a prescription before completion");

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Optional.ofNullable(appointment.getSlot()).ifPresent(slot -> {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepo.save(slot);
        });
    }

    private void handleCancel(Appointment appointment) {
        // 1) Không cho hủy lịch đã hoàn tất
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Completed appointments cannot be cancelled");
        }

        // 2) Chặn hủy nếu còn < 48h trước giờ hẹn
        if (appointment.getAppointmentDate() != null && appointment.getAppointmentTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime apptDateTime = LocalDateTime.of(
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime()
            );
            long hoursUntilAppt = ChronoUnit.HOURS.between(now, apptDateTime);
            if (hoursUntilAppt < 48) {
                throw new IllegalStateException("Cannot cancel within 48 hours before appointment");
            }
        } else {
            throw new IllegalStateException("Appointment date/time is missing; cannot process cancellation");
        }

        // 3) ĐẾM TRƯỚC KHI SET CANCELLED (để tránh JPA flush làm đếm lệch)
        if (appointment.getPatient() != null) {
            Long patientId = appointment.getPatient().getId();

            long existingCancels = appointmentRepo.countByPatientIdAndStatus(
                    patientId, AppointmentStatus.CANCELLED
            );

            long totalCancels = existingCancels + 1; // +1 để tính cả lần hủy đang thực hiện
            if (totalCancels >= 3) {
                User patient = appointment.getPatient();
                patient.setIsEnable(false);
                userRepo.save(patient);
                System.out.println("⚠️ User " + patient.getFullName() + " bị khóa vì hủy lịch " + totalCancels + " lần.");
            }
        }

        // 4) Set trạng thái hủy (đặt SAU khi đếm)
        appointment.setStatus(AppointmentStatus.CANCELLED);

        // 5) Trả slot về AVAILABLE
        Optional.ofNullable(appointment.getSlot()).ifPresent(slot -> {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepo.save(slot);
        });
    }

}
