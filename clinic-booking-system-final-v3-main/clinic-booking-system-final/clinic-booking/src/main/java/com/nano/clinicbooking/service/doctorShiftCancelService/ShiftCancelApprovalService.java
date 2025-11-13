package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;


public interface ShiftCancelApprovalService {
    ShiftCancelRequestDto approveRequest(Long requestId, Long receptionistId);
    ShiftCancelRequestDto rejectRequest(Long requestId, Long receptionistId, String note);
}