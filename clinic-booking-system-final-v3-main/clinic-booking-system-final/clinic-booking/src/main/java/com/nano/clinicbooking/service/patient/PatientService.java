package com.nano.clinicbooking.service.patient;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.repository.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<UserDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .filter(Patient::getIsEnable) // chỉ lấy patient active
                .map(p -> {
                    UserDto dto = new UserDto();
                    dto.setId(p.getId());
                    dto.setFullName(p.getFullName());
                    dto.setEmail(p.getEmail());
                    dto.setPhoneNumber(p.getPhoneNumber());
                    dto.setGender(p.getGender());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
