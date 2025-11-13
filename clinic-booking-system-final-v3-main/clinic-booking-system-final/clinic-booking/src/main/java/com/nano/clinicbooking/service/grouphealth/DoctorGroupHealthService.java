package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.response.groupheath.DoctorGroupAssignmentView;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DoctorGroupHealthService {

    List<DoctorGroupAssignmentView> getAssignmentsForDoctor(Long doctorId, LocalDate date);

    String uploadResultFile(Long requestId, int partIndex, Long doctorId, MultipartFile file) throws Exception;
}
