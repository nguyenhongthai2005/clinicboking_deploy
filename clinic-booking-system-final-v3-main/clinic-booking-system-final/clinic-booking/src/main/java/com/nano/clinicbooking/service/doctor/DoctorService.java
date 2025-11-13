package com.nano.clinicbooking.service.doctor;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;


    public List<UserDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .filter(Doctor::getIsEnable) // chỉ lấy doctor đang active
                .map(doctor -> {
                    UserDto dto = new UserDto();
                    dto.setId(doctor.getId());
                    dto.setFullName(doctor.getFullName());
                    dto.setEmail(doctor.getEmail());
                    dto.setPhoneNumber(doctor.getPhoneNumber());
                    dto.setGender(doctor.getGender());
                    if (doctor.getSpecialty() != null) {
                        dto.setSpecialization(doctor.getSpecialty().getName());
                    }
                    dto.setDegree(doctor.getDegree());
                    dto.setDescription(doctor.getDescription());
                    dto.setExperience(doctor.getExperience());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
