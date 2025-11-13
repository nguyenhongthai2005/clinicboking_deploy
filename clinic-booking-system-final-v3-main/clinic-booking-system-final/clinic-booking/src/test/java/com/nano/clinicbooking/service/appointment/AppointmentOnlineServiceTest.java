package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.enums.AppointmentType;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentOnlineServiceTest {

    private AppointmentRepository appointmentRepo;
    private AppointmentQueryService queryService;
    private AppointmentOnlineService service;

    @BeforeEach
    void setUp() {
        appointmentRepo = mock(AppointmentRepository.class);
        queryService = mock(AppointmentQueryService.class);
        // ModelMapper chưa dùng nên truyền null
        service = new AppointmentOnlineService(appointmentRepo, queryService, null);
    }

    // =================== TC1 – Success get meeting info ===================
    @Test
    void getOnlineMeetingInfoForPatient_Success() {
        Long appointmentId = 1L;
        Long patientId = 10L;

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setPatient(patient);
        app.setType(AppointmentType.ONLINE);
        app.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));
        AppointmentDto expectedDto = new AppointmentDto();
        expectedDto.setId(appointmentId);
        when(queryService.getAppointmentById(appointmentId)).thenReturn(expectedDto);

        AppointmentDto result = service.getOnlineMeetingInfoForPatient(appointmentId, patientId);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        verify(appointmentRepo, times(1)).findById(appointmentId);
        verify(queryService, times(1)).getAppointmentById(appointmentId);
    }

    // ========== TC2 – Sai patient, không được phép xem ==========
    @Test
    void getOnlineMeetingInfoForPatient_WrongPatient_ShouldThrow() {
        Long appointmentId = 1L;
        Long realPatientId = 10L;
        Long wrongPatientId = 99L;

        Patient patient = new Patient();
        patient.setId(realPatientId);

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setPatient(patient);
        app.setType(AppointmentType.ONLINE);
        app.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));

        assertThrows(IllegalStateException.class, () ->
                service.getOnlineMeetingInfoForPatient(appointmentId, wrongPatientId));

        verify(appointmentRepo, times(1)).findById(appointmentId);
        verifyNoInteractions(queryService);
    }

    // ========== TC3 – Appointment không phải ONLINE ==========
    @Test
    void getOnlineMeetingInfoForPatient_NotOnline_ShouldThrow() {
        Long appointmentId = 1L;
        Long patientId = 10L;

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setPatient(patient);
        app.setType(AppointmentType.OFFLINE); // OFFLINE
        app.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));

        assertThrows(IllegalStateException.class, () ->
                service.getOnlineMeetingInfoForPatient(appointmentId, patientId));

        verify(appointmentRepo, times(1)).findById(appointmentId);
        verifyNoInteractions(queryService);
    }

    // ========== TC4 – Appointment chưa được CONFIRMED ==========
    @Test
    void getOnlineMeetingInfoForPatient_NotConfirmed_ShouldThrow() {
        Long appointmentId = 1L;
        Long patientId = 10L;

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setPatient(patient);
        app.setType(AppointmentType.ONLINE);
        // bệnh nhân mới đặt, đang chờ lễ tân duyệt
        app.setStatus(AppointmentStatus.PENDING_CONFIRMATION);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.getOnlineMeetingInfoForPatient(appointmentId, patientId));

        assertEquals("Appointment is not confirmed yet", ex.getMessage());
        verify(appointmentRepo, times(1)).findById(appointmentId);
        verifyNoInteractions(queryService);
    }

    // ========== TC5 – Appointment không tồn tại (get info) ==========
    @Test
    void getOnlineMeetingInfoForPatient_NotFound_ShouldThrow() {
        Long appointmentId = 999L;
        Long patientId = 10L;

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                service.getOnlineMeetingInfoForPatient(appointmentId, patientId));

        verify(appointmentRepo, times(1)).findById(appointmentId);
        verifyNoInteractions(queryService);
    }

    // ========== TC6 – Start consultation khi CONFIRMED ==========
    @Test
    void startOnlineConsultation_Confirmed_Success() {
        Long appointmentId = 1L;

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setType(AppointmentType.ONLINE);
        app.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));
        AppointmentDto expected = new AppointmentDto();
        expected.setId(appointmentId);
        when(queryService.getAppointmentById(appointmentId)).thenReturn(expected);

        AppointmentDto result = service.startOnlineConsultation(appointmentId);

        assertNotNull(result);
        assertEquals(AppointmentStatus.IN_PROGRESS, app.getStatus());
        assertNotNull(app.getActualStartTime());
        verify(appointmentRepo, times(1)).save(app);
        verify(queryService, times(1)).getAppointmentById(appointmentId);
    }

    // ========== TC7 – Start consultation khi CHECKED_IN ==========
    @Test
    void startOnlineConsultation_CheckedIn_Success() {
        Long appointmentId = 2L;

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setType(AppointmentType.ONLINE);
        app.setStatus(AppointmentStatus.CHECKED_IN);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));
        AppointmentDto expected = new AppointmentDto();
        expected.setId(appointmentId);
        when(queryService.getAppointmentById(appointmentId)).thenReturn(expected);

        AppointmentDto result = service.startOnlineConsultation(appointmentId);

        assertNotNull(result);
        assertEquals(AppointmentStatus.IN_PROGRESS, app.getStatus());
        assertNotNull(app.getActualStartTime());
        verify(appointmentRepo, times(1)).save(app);
        verify(queryService, times(1)).getAppointmentById(appointmentId);
    }

    // ========== TC8 – Start consultation: appointment không phải ONLINE ==========
    @Test
    void startOnlineConsultation_NotOnline_ShouldThrow() {
        Long appointmentId = 1L;

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setType(AppointmentType.OFFLINE); // không phải ONLINE
        app.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));

        assertThrows(IllegalStateException.class,
                () -> service.startOnlineConsultation(appointmentId));

        verify(appointmentRepo, times(1)).findById(appointmentId);
        verify(appointmentRepo, never()).save(any());
        verifyNoInteractions(queryService);
    }

    // ========== TC9 – Start consultation: status không hợp lệ ==========
    @Test
    void startOnlineConsultation_InvalidStatus_ShouldThrow() {
        Long appointmentId = 1L;

        Appointment app = new Appointment();
        app.setId(appointmentId);
        app.setType(AppointmentType.ONLINE);
        // chưa confirm, đang ở PENDING_CONFIRMATION
        app.setStatus(AppointmentStatus.PENDING_CONFIRMATION);

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(app));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.startOnlineConsultation(appointmentId));

        assertEquals("Appointment must be confirmed before starting", ex.getMessage());
        verify(appointmentRepo, times(1)).findById(appointmentId);
        verify(appointmentRepo, never()).save(any());
        verifyNoInteractions(queryService);
    }

    // ========== TC10 – Start consultation: appointment không tồn tại ==========
    @Test
    void startOnlineConsultation_NotFound_ShouldThrow() {
        Long appointmentId = 999L;

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.startOnlineConsultation(appointmentId));

        verify(appointmentRepo, times(1)).findById(appointmentId);
        verifyNoInteractions(queryService);
    }
}
