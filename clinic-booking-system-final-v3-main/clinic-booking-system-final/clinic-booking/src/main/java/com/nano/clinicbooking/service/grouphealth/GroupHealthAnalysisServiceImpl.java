package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.response.*;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthAnalysisResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthMemberInfo;
import com.nano.clinicbooking.enums.ShiftStatus;
import com.nano.clinicbooking.model.*;
import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import com.nano.clinicbooking.repository.*;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.doctor.DoctorShiftRepository;
import com.nano.clinicbooking.repository.grouphealth.GroupHealthRequestRepository;
import com.nano.clinicbooking.utils.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupHealthAnalysisServiceImpl implements GroupHealthAnalysisService {

    private final GroupHealthRequestRepository groupHealthRequestRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorShiftRepository doctorShiftRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public GroupHealthAnalysisResponse analyze(Long requestId) throws IOException {
        GroupHealthRequest request = groupHealthRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        // ✅ 1. Đọc Excel
        Path excelPath = Paths.get(request.getExcelFilePath());
        List<GroupHealthMemberInfo> members = ExcelParser.parseMembers(excelPath.toString());

        // ✅ 2. Xác định chuyên khoa
        List<String> departmentNames = Arrays.stream(request.getDepartments().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        List<Specialty> specialties = specialtyRepository.findByNameIn(departmentNames);
        List<Long> specialtyIds = specialties.stream().map(Specialty::getId).toList();


        // ✅ 3. Gợi ý bác sĩ
        List<DoctorSuggestionDto> suggestions = new ArrayList<>();

        for (Long sid : specialtyIds) {
            List<Doctor> doctors = doctorRepository.findBySpecialtyId(sid);
            log.info("🔍 Specialty {} có {} bác sĩ.", sid, doctors.size());

            for (Doctor doctor : doctors) {
                log.info("👨‍⚕️ Xét bác sĩ: {} (ID={}) - chuyên khoa: {}",
                        doctor.getFullName(), doctor.getId(), doctor.getSpecialty().getName());

                List<DoctorShift> shifts = doctorShiftRepository.findByDoctorIdAndDate(
                        doctor.getId(), request.getPreferredDate());
                log.info("🗓️ Bác sĩ {} có {} ca trực vào ngày {}",
                        doctor.getFullName(), shifts.size(), request.getPreferredDate());

                for (DoctorShift shift : shifts) {
                    long booked = appointmentRepository.countByShiftId(shift.getId());
                    int available = shift.getMaxPatients() - (int) booked;

                    log.info("➡️ Shift {} [{}] - status={}, maxPatients={}, booked={}, available={}",
                            shift.getId(), shift.getShift(), shift.getStatus(),
                            shift.getMaxPatients(), booked, available);

                    if (shift.getStatus() != ShiftStatus.ACTIVE) continue;
                    if (available > 0) {
                        suggestions.add(DoctorSuggestionDto.builder()
                                .doctorId(doctor.getId())
                                .doctorName(doctor.getFullName())
                                .specialtyName(doctor.getSpecialty().getName())
                                .shiftId(shift.getId())
                                .shiftDate(shift.getDate())
                                .shiftType(shift.getShift().name())
                                .maxPatients(shift.getMaxPatients())
                                .slotsAvailable(available)
                                .build());
                        log.info("✅ Gợi ý bác sĩ {} - ca {} ({}) còn {} slot trống.",
                                doctor.getFullName(), shift.getId(), shift.getShift(), available);
                    }
                }
            }
        }

        log.info("📊 Tổng số gợi ý bác sĩ được tạo: {}", suggestions.size());



        // ✅ 3. Gợi ý bác sĩ
//        List<DoctorSuggestionDto> suggestions = new ArrayList<>();
//
//        for (Long sid : specialtyIds) {
//            List<Doctor> doctors = doctorRepository.findBySpecialtyId(sid);
//            for (Doctor doctor : doctors) {
//                List<DoctorShift> shifts = doctorShiftRepository.findByDoctorIdAndDate(doctor.getId(), request.getPreferredDate());
//                for (DoctorShift shift : shifts) {
//                    if (shift.getStatus() != ShiftStatus.ACTIVE) continue;
//                    long booked = appointmentRepository.countByShiftId(shift.getId());
//                    int available = shift.getMaxPatients() - (int) booked;
//                    if (available > 0) {
//                        suggestions.add(DoctorSuggestionDto.builder()
//                                .doctorId(doctor.getId())
//                                .doctorName(doctor.getFullName())
//                                .specialtyName(doctor.getSpecialty().getName())
//                                .shiftId(shift.getId())
//                                .shiftDate(shift.getDate())
//                                .shiftType(shift.getShift().name())
//                                .maxPatients(shift.getMaxPatients())
//                                .slotsAvailable(available)
//                                .build());
//                    }
//                }
//            }
//        }

        return GroupHealthAnalysisResponse.builder()
                .requestId(request.getId())
                .groupName(request.getGroupName())
                .departments(request.getDepartments())
                .preferredDate(request.getPreferredDate())
                .totalMembers(members.size())
                .members(members)
                .suggestedDoctors(suggestions)
                .build();
    }
}
