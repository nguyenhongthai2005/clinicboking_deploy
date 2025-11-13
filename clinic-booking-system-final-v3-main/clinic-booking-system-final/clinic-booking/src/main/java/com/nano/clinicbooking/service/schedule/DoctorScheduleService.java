package com.nano.clinicbooking.service.schedule;

import com.nano.clinicbooking.dto.response.doctorshedule.DoctorWeeklyNamesResponse;

import java.time.LocalDate;

public interface DoctorScheduleService {
    DoctorWeeklyNamesResponse getWeeklySchedule(Long specialtyId, LocalDate weekStart);
}
