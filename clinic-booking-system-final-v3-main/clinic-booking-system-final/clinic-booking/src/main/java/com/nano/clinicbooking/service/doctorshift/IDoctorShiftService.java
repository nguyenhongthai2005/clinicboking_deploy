package com.nano.clinicbooking.service.doctorshift;

import com.nano.clinicbooking.dto.response.DoctorShiftDto;
import com.nano.clinicbooking.model.DoctorShift;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface IDoctorShiftService {
    DoctorShift createShift(Long doctorId, DoctorShift shift);
    List<DoctorShift> getShiftsByDoctor(Long doctorId);


    @Transactional(readOnly = true)
    List<DoctorShiftDto> getShiftsByDoctorAndDate(Long doctorId, LocalDate date);

    // ✅ API 2: Lấy tất cả ca (mọi bác sĩ) trong ngày
    @Transactional(readOnly = true)
    List<DoctorShiftDto> getAllShiftsByDate(LocalDate date);
}