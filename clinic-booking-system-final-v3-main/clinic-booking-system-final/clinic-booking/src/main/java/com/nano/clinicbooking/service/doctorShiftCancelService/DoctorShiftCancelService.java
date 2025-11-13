package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;


public interface DoctorShiftCancelService {
    ShiftCancelRequestDto createCancelRequest(Long doctorId, Long shiftId, String reason);
}