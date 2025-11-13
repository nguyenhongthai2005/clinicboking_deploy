package com.nano.clinicbooking.service.doctorshift;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.dto.response.DoctorShiftDto;
import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.dto.response.ShiftSlotDto;
import com.nano.clinicbooking.enums.SlotStatus;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.model.ShiftSlot;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.shiftSlot.ShiftSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorShiftService implements IDoctorShiftService {

    private final DoctorRepository doctorRepo;
    private final DoctorShiftRepository shiftRepo;
    private final ShiftSlotRepository slotRepo;
    private final EntityConverter<DoctorShift, DoctorShiftDto> converter;



    @Transactional
    @Override
    public DoctorShift createShift(Long doctorId, DoctorShift shift) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        shift.setDoctor(doctor);

        // Auto giờ theo loại ca nếu chưa set
        if (shift.getShift() != null) {
            shift.setStartTime(shift.getShift().getStartTime());
            shift.setEndTime(shift.getShift().getEndTime());
        }

        // ✅ Tạo ca đầu tiên
        DoctorShift firstShift = shiftRepo.save(shift);
        createSlotsForShift(firstShift);

        // ✅ Nếu có repeat weekly → tạo thêm các tuần tiếp theo
        if (Boolean.TRUE.equals(shift.getRepeatWeekly()) && shift.getRepeatCount() != null && shift.getRepeatCount() > 0) {
            for (int i = 1; i <= shift.getRepeatCount(); i++) {
                DoctorShift nextWeekShift = new DoctorShift();

                // ✅ Chỉ copy các field đơn giản, KHÔNG copy collection
                nextWeekShift.setDate(shift.getDate().plusWeeks(i));
                nextWeekShift.setShift(shift.getShift());
                nextWeekShift.setStartTime(shift.getStartTime());
                nextWeekShift.setEndTime(shift.getEndTime());
                nextWeekShift.setMaxPatients(shift.getMaxPatients());
                nextWeekShift.setRepeatWeekly(false); // tránh đệ quy
                nextWeekShift.setDoctor(doctor);
                nextWeekShift.setNote(shift.getNote());

                DoctorShift saved = shiftRepo.save(nextWeekShift);
                createSlotsForShift(saved);
            }
        }


        return firstShift;
    }

    /**
     * 🕒 Sinh slot 45p + buffer 15p trong ca
     */
    private void createSlotsForShift(DoctorShift shift) {
        LocalTime current = shift.getStartTime();
        int slotNumber = 1;

        while (current.plusMinutes(45).isBefore(shift.getEndTime()) ||
                current.plusMinutes(45).equals(shift.getEndTime())) {

            ShiftSlot slot = new ShiftSlot();
            slot.setSlotNumber(slotNumber++);
            slot.setStartTime(current);
            slot.setEndTime(current.plusMinutes(45));
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setShift(shift);

            slotRepo.save(slot);
            // +15 phút buffer
            current = current.plusMinutes(60);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorShift> getShiftsByDoctor(Long doctorId) {
        return shiftRepo.findByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorShiftDto> getShiftsByDoctorAndDate(Long doctorId, LocalDate date) {
        List<DoctorShift> shifts = shiftRepo.findByDoctorIdAndDate(doctorId, date);
        return mapShiftsWithAppointments(shifts);
    }

    // ✅ API 2: Lấy tất cả ca (mọi bác sĩ) trong ngày
    @Transactional(readOnly = true)
    @Override
    public List<DoctorShiftDto> getAllShiftsByDate(LocalDate date) {
        List<DoctorShift> shifts = shiftRepo.findByDate(date);
        return mapShiftsWithAppointments(shifts);
    }

    // ⚙️ Map dữ liệu chung cho 2 API trên
    private List<DoctorShiftDto> mapShiftsWithAppointments(List<DoctorShift> shifts) {
        return shifts.stream().map(shift -> {
            DoctorShiftDto dto = new DoctorShiftDto();
            dto.setId(shift.getId());
            dto.setDoctorId(shift.getDoctor().getId());
            dto.setDoctorName(shift.getDoctor().getFullName());
            dto.setSpecialtyName(shift.getDoctor().getSpecialty() != null ? shift.getDoctor().getSpecialty().getName() : null);
            dto.setDate(shift.getDate());
            dto.setShift(shift.getShift());
            dto.setStartTime(shift.getStartTime());
            dto.setEndTime(shift.getEndTime());
            dto.setMaxPatients(shift.getMaxPatients());
            dto.setNote(shift.getNote());

            // ✅ Gắn danh sách slot và appointment
            dto.setSlots(shift.getSlots().stream().map(slot -> {
                ShiftSlotDto s = new ShiftSlotDto();
                s.setId(slot.getId());
                s.setSlotNumber(slot.getSlotNumber());
                s.setStartTime(slot.getStartTime());
                s.setEndTime(slot.getEndTime());
                s.setStatus(slot.getStatus());

                // Nếu slot có appointment
                Appointment app = slot.getAppointment();
                if (app != null) {
                    AppointmentDto a = new AppointmentDto();
                    a.setId(app.getId());
                    a.setPatientName(app.getPatient().getFullName());
                    a.setStatus(app.getStatus());
                    a.setReason(app.getReason());
                    a.setAppointmentTime(app.getAppointmentTime());
                    s.setAppointment(a);
                }

                return s;
            }).toList());

            return dto;
        }).toList();
    }





}
