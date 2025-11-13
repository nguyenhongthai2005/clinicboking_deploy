package com.nano.clinicbooking.service.doctorShiftCancelService;

import com.nano.clinicbooking.dto.request.ShiftCancelRequestDto;
import com.nano.clinicbooking.enums.ShiftCancelStatus;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.ShiftCancelRequest;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.shiftSlot.ShiftCancelRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DoctorShiftCancelServiceImpl implements DoctorShiftCancelService {

    private final DoctorRepository doctorRepository;
    private final DoctorShiftRepository shiftRepository;
    private final ShiftCancelRequestRepository cancelRequestRepository;

    @Override
    @Transactional
    public ShiftCancelRequestDto createCancelRequest(Long doctorId, Long shiftId, String reason) {

        // 1. Lấy bác sĩ
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ ID=" + doctorId));

        // 2. Lấy ca làm việc
        DoctorShift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ca làm ID=" + shiftId));

        // 3. Kiểm tra quyền
        if (!shift.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalStateException("Ca làm này không thuộc về bác sĩ hiện tại!");
        }

        // 4. Tránh gửi trùng
        if (cancelRequestRepository.existsByShiftIdAndStatus(shiftId, ShiftCancelStatus.PENDING)) {
            throw new IllegalStateException("Ca làm này đã có yêu cầu hủy đang chờ duyệt!");
        }

        // 5. Tạo entity mới
        ShiftCancelRequest entity = new ShiftCancelRequest();
        entity.setDoctor(doctor);
        entity.setShift(shift);
        entity.setReason(reason);
        entity.setStatus(ShiftCancelStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        cancelRequestRepository.save(entity);

        // 6. Trả DTO gọn
        ShiftCancelRequestDto dto = new ShiftCancelRequestDto();
        dto.setShiftId(shift.getId());
        dto.setReason(reason);

        return dto;
    }
}
