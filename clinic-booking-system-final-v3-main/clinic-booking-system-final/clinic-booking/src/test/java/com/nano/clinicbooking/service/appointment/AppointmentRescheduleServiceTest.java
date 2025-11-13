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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentRescheduleServiceTest {

    private AppointmentRepository appointmentRepo;
    private DoctorShiftRepository shiftRepo;
    private ShiftSlotRepository slotRepo;
    private AppointmentMapper mapper;
    private AppointmentRescheduleService service;

    @BeforeEach
    void setUp() {
        appointmentRepo = mock(AppointmentRepository.class);
        shiftRepo = mock(DoctorShiftRepository.class);
        slotRepo = mock(ShiftSlotRepository.class);
        mapper = new AppointmentMapper();
        service = new AppointmentRescheduleService(appointmentRepo, shiftRepo, slotRepo, mapper);
    }

    @Test
    void rescheduleAppointment_success() {
        Long appointmentId = 1L;
        Long newShiftId = 2L;

        // Mock dữ liệu
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        DoctorShift newShift = new DoctorShift();
        newShift.setId(newShiftId);

        ShiftSlot oldSlot = new ShiftSlot();
        oldSlot.setStatus(SlotStatus.BOOKED);
        appointment.setSlot(oldSlot);

        ShiftSlot newSlot = new ShiftSlot();
        newSlot.setId(10L);
        newSlot.setStatus(SlotStatus.AVAILABLE);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(shiftRepo.findById(newShiftId)).thenReturn(Optional.of(newShift));
        when(slotRepo.findFirstByShiftIdAndStatusOrderBySlotNumberAsc(newShiftId, SlotStatus.AVAILABLE))
                .thenReturn(Optional.of(newSlot));

        AppointmentDto result = service.rescheduleAppointment(appointmentId, newShiftId);

        // Kiểm tra logic chính
        assertEquals(AppointmentStatus.RESCHEDULED, appointment.getStatus());
        assertEquals(SlotStatus.BOOKED, newSlot.getStatus());
        assertEquals(newShift, appointment.getShift());
        assertNotNull(result);
        verify(slotRepo, times(2)).save(any(ShiftSlot.class)); // save old & new slot
        verify(appointmentRepo).save(appointment);
    }

    @Test
    void rescheduleAppointment_appointmentNotFound() {
        when(appointmentRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.rescheduleAppointment(1L, 2L));
    }

    @Test
    void rescheduleAppointment_shiftNotFound() {
        Appointment appointment = new Appointment();
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
        when(shiftRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.rescheduleAppointment(1L, 2L));
    }

    @Test
    void rescheduleAppointment_noAvailableSlot() {
        Appointment appointment = new Appointment();
        DoctorShift newShift = new DoctorShift();

        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
        when(shiftRepo.findById(2L)).thenReturn(Optional.of(newShift));
        when(slotRepo.findFirstByShiftIdAndStatusOrderBySlotNumberAsc(2L, SlotStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.rescheduleAppointment(1L, 2L));
    }
}