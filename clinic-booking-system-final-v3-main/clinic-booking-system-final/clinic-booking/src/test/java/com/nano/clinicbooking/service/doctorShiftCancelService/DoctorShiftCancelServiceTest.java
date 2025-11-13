package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.enums.ShiftCancelStatus;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.ShiftCancelRequest;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.shiftSlot.ShiftCancelRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorShiftCancelServiceTest {

    private DoctorShiftCancelServiceImpl service;
    private DoctorRepository doctorRepository;
    private DoctorShiftRepository shiftRepository;
    private ShiftCancelRequestRepository cancelRequestRepository;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        shiftRepository = mock(DoctorShiftRepository.class);
        cancelRequestRepository = mock(ShiftCancelRequestRepository.class);
        service = new DoctorShiftCancelServiceImpl(doctorRepository, shiftRepository, cancelRequestRepository);
    }


    @Test
    void testCreateCancelRequest_Success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        DoctorShift shift = new DoctorShift();
        shift.setId(100L);
        shift.setDoctor(doctor);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(shiftRepository.findById(100L)).thenReturn(Optional.of(shift));
        when(cancelRequestRepository.existsByShiftIdAndStatus(100L, ShiftCancelStatus.PENDING))
                .thenReturn(false);

        var dto = service.createCancelRequest(1L, 100L, "Bận việc đột xuất");

        // ✅ Verify
        assertEquals(100L, dto.getShiftId());
        assertEquals("Bận việc đột xuất", dto.getReason());

        // ✅ Check save was called
        ArgumentCaptor<ShiftCancelRequest> captor = ArgumentCaptor.forClass(ShiftCancelRequest.class);
        verify(cancelRequestRepository, times(1)).save(captor.capture());

        ShiftCancelRequest saved = captor.getValue();
        assertEquals(ShiftCancelStatus.PENDING, saved.getStatus());
        assertEquals("Bận việc đột xuất", saved.getReason());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testCreateCancelRequest_WrongDoctor_ShouldThrow() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Doctor anotherDoctor = new Doctor();
        anotherDoctor.setId(2L);

        DoctorShift shift = new DoctorShift();
        shift.setId(100L);
        shift.setDoctor(anotherDoctor);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(shiftRepository.findById(100L)).thenReturn(Optional.of(shift));

        assertThrows(IllegalStateException.class,
                () -> service.createCancelRequest(1L, 100L, "Sai bác sĩ"));
    }
}
