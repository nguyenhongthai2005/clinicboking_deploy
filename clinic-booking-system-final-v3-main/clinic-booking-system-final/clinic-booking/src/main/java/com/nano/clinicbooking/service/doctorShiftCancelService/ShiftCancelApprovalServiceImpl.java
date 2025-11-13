package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;
import com.nano.clinicbooking.enums.ShiftCancelStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.ShiftCancelRequest;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.shiftSlot.ShiftCancelRequestRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShiftCancelApprovalServiceImpl implements ShiftCancelApprovalService {

    private final ShiftCancelRequestRepository cancelRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShiftCancelRequestDto approveRequest(Long requestId, Long receptionistId) {
        // 🔹 1. Tìm request
        ShiftCancelRequest request = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hủy ID=" + requestId));

        // 🔹 2. Tìm receptionist
        User receptionist = userRepository.findById(receptionistId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lễ tân ID=" + receptionistId));

        // 🔹 3. Cập nhật trạng thái
        request.setStatus(ShiftCancelStatus.APPROVED);
        request.setApprovedBy(receptionist);
        request.setApprovedAt(LocalDateTime.now());
        request.setNote("Đã duyệt yêu cầu hủy của bác sĩ.");

        cancelRequestRepository.save(request);

        // 🔹 4. Trả về DTO cơ bản
        ShiftCancelRequestDto dto = new ShiftCancelRequestDto();
        dto.setShiftId(request.getShift().getId());
        dto.setReason(request.getReason());
        return dto;
    }

    @Override
    @Transactional
    public ShiftCancelRequestDto rejectRequest(Long requestId, Long receptionistId, String note) {
        // 🔹 1. Tìm request
        ShiftCancelRequest request = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hủy ID=" + requestId));

        // 🔹 2. Tìm lễ tân
        User receptionist = userRepository.findById(receptionistId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lễ tân ID=" + receptionistId));

        // 🔹 3. Cập nhật trạng thái
        request.setStatus(ShiftCancelStatus.REJECTED);
        request.setApprovedBy(receptionist);
        request.setApprovedAt(LocalDateTime.now());
        request.setNote(note);

        cancelRequestRepository.save(request);

        // 🔹 4. Trả về DTO cơ bản
        ShiftCancelRequestDto dto = new ShiftCancelRequestDto();
        dto.setShiftId(request.getShift().getId());
        dto.setReason(request.getReason());
        return dto;
    }
}
