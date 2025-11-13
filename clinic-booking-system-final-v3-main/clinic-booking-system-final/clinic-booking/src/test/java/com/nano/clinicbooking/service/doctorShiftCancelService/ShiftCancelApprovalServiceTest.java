package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;
import com.nano.clinicbooking.enums.ShiftCancelStatus;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.ShiftCancelRequest;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.shiftSlot.ShiftCancelRequestRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftCancelApprovalServiceTest {

    @Mock
    private ShiftCancelRequestRepository cancelRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ShiftCancelApprovalServiceImpl service;

    private ShiftCancelRequest request;
    private User receptionist;
    private DoctorShift shift;

    @BeforeEach
    void setUp() {
        // Mock dữ liệu cơ bản
        shift = new DoctorShift();
        shift.setId(100L);

        receptionist = new User();
        receptionist.setId(10L);
        receptionist.setFullName("Receptionist A");

        request = new ShiftCancelRequest();
        request.setId(1L);
        request.setStatus(ShiftCancelStatus.PENDING);
        request.setReason("Bận đột xuất");
        request.setShift(shift);
    }

    @Test
    void testApproveRequest_Success() {
        // Arrange
        when(cancelRepo.findById(1L)).thenReturn(Optional.of(request));
        when(userRepo.findById(10L)).thenReturn(Optional.of(receptionist));

        // Act
        ShiftCancelRequestDto result = service.approveRequest(1L, 10L);

        // Assert
        assertEquals(ShiftCancelStatus.APPROVED, request.getStatus());
        assertEquals(shift.getId(), result.getShiftId());
        assertEquals("Bận đột xuất", result.getReason());
        verify(cancelRepo, times(1)).save(request);
    }

    @Test
    void testRejectRequest_Success() {
        // Arrange
        when(cancelRepo.findById(1L)).thenReturn(Optional.of(request));
        when(userRepo.findById(10L)).thenReturn(Optional.of(receptionist));

        // Act
        ShiftCancelRequestDto result = service.rejectRequest(1L, 10L, "Không hợp lệ");

        // Assert
        assertEquals(ShiftCancelStatus.REJECTED, request.getStatus());
        assertEquals("Không hợp lệ", request.getNote());
        assertEquals(shift.getId(), result.getShiftId());
        verify(cancelRepo, times(1)).save(request);
    }
}
