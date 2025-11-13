package com.nano.clinicbooking.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nano.clinicbooking.dto.response.EventResponse;
import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.dto.response.DoctorShiftDto;
import com.nano.clinicbooking.service.doctor.DoctorService;
import com.nano.clinicbooking.service.doctorshift.DoctorShiftService;
import com.nano.clinicbooking.service.ev_vc.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lấy dữ liệu từ DB → trả JSON text cho AI dùng.
 */
@Service
@RequiredArgsConstructor
public class UnifiedClinicDataProvider {

    private final EventService eventService;
    private final DoctorService doctorService;
    private final DoctorShiftService doctorShiftService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /** JSON sự kiện (enable=true) */
    public String getEventsJson() {
        try {
            List<EventResponse> events = eventService.getAllEvents(); // đã filter enabled=true
            DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;

            var list = events.stream().map(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("title", e.getTitle());
                m.put("startDate", e.getStartDate() != null ? iso.format(e.getStartDate()) : null);
                m.put("endDate", e.getEndDate() != null ? iso.format(e.getEndDate()) : null);
                m.put("location", e.getLocation());
                m.put("description", e.getDescription());

                if (e.getVoucherCode() != null || e.getVoucherId() != null) {
                    Map<String, Object> v = new LinkedHashMap<>();
                    v.put("id", e.getVoucherId());
                    v.put("code", e.getVoucherCode());
                    v.put("name", e.getVoucherName());
                    m.put("voucher", v);
                }
                return m;
            }).toList();

            return mapper.writeValueAsString(list);
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi build JSON sự kiện", ex);
        }
    }

    /**
     * ✅ SỬA ĐÚNG: dùng getAllShiftsByDate(LocalDate) thay vì getShiftsByDate(...)
     * Trả JSON ca khám trong ngày, kèm slot (nếu muốn).
     */
    public String getShiftsJson(LocalDate date) {
        try {
            List<DoctorShiftDto> shifts = doctorShiftService.getAllShiftsByDate(date);

            var list = shifts.stream().map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("shiftId", s.getId());
                m.put("date", s.getDate());                       // LocalDate -> đã có JavaTimeModule
                m.put("doctorId", s.getDoctorId());
                m.put("doctorName", s.getDoctorName());
                m.put("specialty", s.getSpecialtyName());
                m.put("shiftType", s.getShift());                 // enum ca (nếu có)
                m.put("startTime", s.getStartTime());
                m.put("endTime", s.getEndTime());
                m.put("maxPatients", s.getMaxPatients());
                m.put("note", s.getNote());

                // (tuỳ chọn) đính kèm slots
                if (s.getSlots() != null && !s.getSlots().isEmpty()) {
                    var slots = s.getSlots().stream().map(slot -> {
                        Map<String, Object> sm = new LinkedHashMap<>();
                        sm.put("slotId", slot.getId());
                        sm.put("slotNumber", slot.getSlotNumber());
                        sm.put("startTime", slot.getStartTime());
                        sm.put("endTime", slot.getEndTime());
                        sm.put("status", slot.getStatus());       // AVAILABLE/BOOKED/...
                        if (slot.getAppointment() != null) {
                            var a = slot.getAppointment();
                            Map<String, Object> am = new LinkedHashMap<>();
                            am.put("id", a.getId());
                            am.put("patientName", a.getPatientName());
                            am.put("status", a.getStatus());
                            am.put("reason", a.getReason());
                            am.put("appointmentTime", a.getAppointmentTime());
                            sm.put("appointment", am);
                        }
                        return sm;
                    }).toList();
                    m.put("slots", slots);
                }
                return m;
            }).toList();

            return mapper.writeValueAsString(list);
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi build JSON lịch khám", ex);
        }
    }

    /** JSON danh sách bác sĩ (enable=true) */
    public String getDoctorsJson() {
        try {
            List<UserDto> doctors = doctorService.getAllDoctors();
            var list = doctors.stream().map(d -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", d.getId());
                m.put("fullName", d.getFullName());
                m.put("specialty", d.getSpecialization()); // "Mắt", "Tâm thần", "TMH", ...
                m.put("degree", d.getDegree());
                m.put("experienceYears", d.getExperience());
                m.put("description", d.getDescription());
                m.put("phone", d.getPhoneNumber());
                return m;
            }).toList();

            return mapper.writeValueAsString(list);
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi build JSON bác sĩ", ex);
        }
    }
}
