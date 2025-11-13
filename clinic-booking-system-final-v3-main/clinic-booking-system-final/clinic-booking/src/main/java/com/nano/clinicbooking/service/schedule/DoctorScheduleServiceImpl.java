package com.nano.clinicbooking.service.schedule;

import com.nano.clinicbooking.dto.response.doctorshedule.DayNamesDTO;
import com.nano.clinicbooking.dto.response.doctorshedule.ShiftNamesDTO;
import com.nano.clinicbooking.dto.response.groupheath.*;
import com.nano.clinicbooking.enums.ShiftType;           // MORNING/AFTERNOON/EVENING
import com.nano.clinicbooking.model.DoctorShift;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nano.clinicbooking.dto.response.doctorshedule.DoctorWeeklyNamesResponse;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorShiftRepository doctorShiftRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public DoctorWeeklyNamesResponse getWeeklySchedule(Long specialtyId, LocalDate weekStart) {
        if (specialtyId == null) throw new IllegalArgumentException("specialtyId is required");

        LocalDate start = (weekStart != null) ? mondayOf(weekStart) : mondayOf(LocalDate.now());
        LocalDate end = start.plusDays(6);

        var spec = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new IllegalArgumentException("Specialty not found: " + specialtyId));

        // Lấy ca trực trong tuần
        List<DoctorShift> shifts = doctorShiftRepository.findWeeklySchedule(specialtyId, start, end);

        // Khung 7 ngày x 3 ca, mỗi ô: danh sách tên
        Map<LocalDate, Map<ShiftType, List<String>>> grid = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = start.plusDays(i);
            grid.put(d, new EnumMap<>(ShiftType.class));
            for (ShiftType st : ShiftType.values()) grid.get(d).put(st, new ArrayList<>());
        }

        for (DoctorShift ds : shifts) {
            grid.get(ds.getDate()).get(ds.getShift()).add(ds.getDoctor().getFullName());
        }

        // Bỏ trùng tên nếu có
        grid.replaceAll((date, byShift) -> {
            byShift.replaceAll((st, names) -> names.stream().distinct().collect(Collectors.toList()));
            return byShift;
        });

        // Build DTO
        List<DayNamesDTO> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = start.plusDays(i);
            List<ShiftNamesDTO> rows = List.of(
                    ShiftNamesDTO.builder().shift(ShiftType.MORNING.name()).doctors(grid.get(d).get(ShiftType.MORNING)).build(),
                    ShiftNamesDTO.builder().shift(ShiftType.AFTERNOON.name()).doctors(grid.get(d).get(ShiftType.AFTERNOON)).build(),
                    ShiftNamesDTO.builder().shift(ShiftType.EVENING.name()).doctors(grid.get(d).get(ShiftType.EVENING)).build()
            );
            days.add(DayNamesDTO.builder()
                    .date(d)
                    .weekday(vnWeekLabel(d.getDayOfWeek()))
                    .shifts(rows)
                    .build());
        }

        return DoctorWeeklyNamesResponse.builder()
                .specialtyId(spec.getId())
                .specialtyName(spec.getName())
                .weekStart(start)
                .weekEnd(end)
                .days(days)
                .build();
    }

    private static LocalDate mondayOf(LocalDate any) {
        DayOfWeek dow = any.getDayOfWeek();
        int back = (dow.getValue() + 6) % 7; // MON=1 -> 0, SUN=7 -> 6
        return any.minusDays(back);
    }

    private static String vnWeekLabel(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
}
