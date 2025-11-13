package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.SlotStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.mapper.AppointmentMapper;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.ShiftSlot;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.shiftSlot.ShiftSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentRescheduleService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorShiftRepository shiftRepo;
    private final ShiftSlotRepository slotRepo;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentDto rescheduleAppointment(Long appointmentId, Long newShiftId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        DoctorShift newShift = shiftRepo.findById(newShiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));

        if (appointment.getSlot() != null) {
            ShiftSlot oldSlot = appointment.getSlot();
            oldSlot.setStatus(SlotStatus.AVAILABLE);
            oldSlot.setAppointment(null);
            slotRepo.save(oldSlot);
        }

        ShiftSlot newSlot = slotRepo.findFirstByShiftIdAndStatusOrderBySlotNumberAsc(newShiftId, SlotStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalStateException("No available slot"));
        newSlot.setStatus(SlotStatus.BOOKED);
        newSlot.setAppointment(appointment);
        slotRepo.save(newSlot);

        appointment.setShift(newShift);
        appointment.setSlot(null);
        // Cập nhật ngày và giờ từ shift mới (dùng startTime của shift)
        appointment.setAppointmentDate(newShift.getDate());
        appointment.setAppointmentTime(newShift.getStartTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        appointmentRepo.save(appointment);
        return appointmentMapper.toDto(appointment);
    }
}
