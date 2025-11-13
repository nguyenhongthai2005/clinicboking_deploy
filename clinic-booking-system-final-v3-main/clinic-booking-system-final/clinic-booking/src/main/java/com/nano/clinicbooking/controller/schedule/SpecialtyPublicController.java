package com.nano.clinicbooking.controller.schedule;

import com.nano.clinicbooking.dto.response.doctorshedule.DoctorWeeklyNamesResponse;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.service.schedule.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/specialties")
@RequiredArgsConstructor
public class SpecialtyPublicController {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorScheduleService doctorScheduleService;

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> listSpecialties() {
        var list = specialtyRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(s -> Map.<String,Object>of("id", s.getId(), "name", s.getName()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<DoctorWeeklyNamesResponse> weeklyById(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
    ) {
        return ResponseEntity.ok(doctorScheduleService.getWeeklySchedule(id, weekStart));
    }
}
