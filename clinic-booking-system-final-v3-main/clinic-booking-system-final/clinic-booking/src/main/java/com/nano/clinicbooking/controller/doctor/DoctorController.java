package com.nano.clinicbooking.controller.doctor;

import com.nano.clinicbooking.dto.response.DoctorDto;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorRepository doctorRepo;

    @PreAuthorize("hasAnyAuthority('Patient','Receptionist','Admin')")
    @GetMapping("/by-specialty/{specialtyId}")
    public ResponseEntity<ApiResponse> getBySpecialty(@PathVariable Long specialtyId) {
        List<Doctor> doctors = doctorRepo.findBySpecialtyId(specialtyId);
        List<DoctorDto> dtos = doctors.stream().map(d -> {
            DoctorDto dto = new DoctorDto();
            dto.setId(d.getId());
            dto.setFullName(d.getFullName());
            dto.setEmail(d.getEmail());
            dto.setPhoneNumber(d.getPhoneNumber());
            dto.setGender(d.getGender());
            dto.setDegree(d.getDegree());
            dto.setDescription(d.getDescription());
            dto.setExperience(d.getExperience());
            if (d.getSpecialty() != null) {
                dto.setSpecialtyId(d.getSpecialty().getId());
                dto.setSpecialtyName(d.getSpecialty().getName());
            }
            return dto;
        }).toList();
        return ResponseEntity.ok(new ApiResponse("Doctors by specialty", dtos));
    }

    @PreAuthorize("hasAnyAuthority('Patient','Receptionist','Admin','Doctor')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDetail(@PathVariable Long id) {
        Doctor d = doctorRepo.findWithSpecialtyById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorDto dto = new DoctorDto();
        dto.setId(d.getId());
        dto.setFullName(d.getFullName());
        dto.setEmail(d.getEmail());
        dto.setPhoneNumber(d.getPhoneNumber());
        dto.setGender(d.getGender());
        dto.setDegree(d.getDegree());
        dto.setDescription(d.getDescription());
        dto.setExperience(d.getExperience());
        if (d.getSpecialty() != null) {
            dto.setSpecialtyId(d.getSpecialty().getId());
            dto.setSpecialtyName(d.getSpecialty().getName());
        }
        return ResponseEntity.ok(new ApiResponse("Doctor detail", dto));
    }


}

