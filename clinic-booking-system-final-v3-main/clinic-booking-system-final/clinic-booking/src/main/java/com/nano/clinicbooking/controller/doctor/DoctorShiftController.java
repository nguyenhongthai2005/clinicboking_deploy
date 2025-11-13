package com.nano.clinicbooking.controller.doctor;

import com.nano.clinicbooking.dto.response.DoctorShiftDto;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.dto.response.ApiResponse;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.service.doctorshift.IDoctorShiftService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class DoctorShiftController {

    private final IDoctorShiftService shiftService;
    private final DoctorShiftRepository shiftRepo;

    @PreAuthorize("hasAuthority('Doctor')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createShift(HttpServletRequest req, @RequestBody DoctorShift shift) {
        Long doctorId = (Long) req.getAttribute("userId");
        DoctorShift created = shiftService.createShift(doctorId, shift);
        return ResponseEntity.ok(new ApiResponse("Shift created successfully", created.getId()));
    }

    @PreAuthorize("hasAuthority('Doctor')")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyShifts(HttpServletRequest req,
                                                   @RequestParam(required=false) String week) {
        Long doctorId = (Long) req.getAttribute("userId");

        List<DoctorShiftDto> dtos = shiftService.getShiftsByDoctor(doctorId)
                .stream()
                .map(shift -> {
                    DoctorShiftDto dto = new DoctorShiftDto();
                    dto.setId(shift.getId());
                    dto.setDoctorId(doctorId);
                    dto.setDoctorName("You");
                    dto.setDate(shift.getDate());
                    dto.setShift(shift.getShift());
                    dto.setStartTime(shift.getStartTime());
                    dto.setEndTime(shift.getEndTime());
                    dto.setMaxPatients(shift.getMaxPatients());
                    dto.setNote(shift.getNote());
                    return dto;
                }).toList();

        // lọc theo tuần (tuỳ chọn)
        if ("current".equalsIgnoreCase(week) || "next".equalsIgnoreCase(week)) {
            LocalDate today = LocalDate.now();
            LocalDate start = "next".equalsIgnoreCase(week)
                    ? today.plusWeeks(1).with(java.time.DayOfWeek.MONDAY)
                    : today.with(java.time.DayOfWeek.MONDAY);
            LocalDate end = start.plusDays(6);
            dtos = dtos.stream()
                    .filter(d -> !d.getDate().isBefore(start) && !d.getDate().isAfter(end))
                    .toList();
        }

        return ResponseEntity.ok(new ApiResponse("Your registered shifts", dtos));
    }

    // ✅ API 1: Lấy ca của bác sĩ theo ngày
    @PreAuthorize("hasAnyAuthority('Doctor','Receptionist','Admin','Patient')")
    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<ApiResponse> getShiftsByDoctorAndDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<DoctorShiftDto> list = shiftService.getShiftsByDoctorAndDate(doctorId, date);
        return ResponseEntity.ok(new ApiResponse("Doctor's shifts for " + date, list));
    }

    // ✅ API 2: Lấy tất cả ca của tất cả bác sĩ trong ngày
    @PreAuthorize("hasAnyAuthority('Receptionist','Admin')")
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse> getAllShiftsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<DoctorShiftDto> list = shiftService.getAllShiftsByDate(date);
        return ResponseEntity.ok(new ApiResponse("Shifts for all doctors on " + date, list));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/admin/week")
    public ResponseEntity<ApiResponse> getShiftsInWeek(@RequestParam String start,
                                                       @RequestParam String end) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        List<DoctorShift> list = shiftRepo.findByDateBetween(s, e);
        record ShiftAdminDto(Long id, String date, String shiftType, Long doctorId, String doctorName, String specialtyName) {}
        List<ShiftAdminDto> dtos = list.stream().map(shift -> {
            Doctor d = shift.getDoctor();
            return new ShiftAdminDto(
                    shift.getId(),
                    shift.getDate().toString(),
                    shift.getShift().name(),
                    d!=null? d.getId(): null,
                    d!=null? d.getFullName(): null,
                    (d!=null && d.getSpecialty()!=null)? d.getSpecialty().getName(): null
            );
        }).toList();
        return ResponseEntity.ok(new ApiResponse("Shifts in week", dtos));
    }


}
