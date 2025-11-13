package com.nano.clinicbooking.controller.patient;

import com.nano.clinicbooking.dto.response.PatientInfoDto;
import com.nano.clinicbooking.model.PatientInformation;
import com.nano.clinicbooking.dto.response.ApiResponse;
import com.nano.clinicbooking.service.patientInformation.IPatientInformationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients-info")
@RequiredArgsConstructor
public class PatientInformationController {

    private final IPatientInformationService patientService;

    /**
     * 🟢 GET ALL (Admin, Receptionist)
     */
    @PreAuthorize("hasAnyAuthority('Admin','Receptionist')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll() {
        List<PatientInformation> list = patientService.getAll();
        return ResponseEntity.ok(new ApiResponse("All patient information", list));
    }

    /**
     * 🟡 GET MY PATIENTS (Patient)
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('Patient','Admin')")
    public ResponseEntity<ApiResponse> getMyPatients(HttpServletRequest req) {
        Long ownerId = (Long) req.getAttribute("userId");
        List<PatientInformation> list = patientService.getByOwner(ownerId);
        List<PatientInfoDto> dtos = list.stream().map(info -> {
            PatientInfoDto dto = new PatientInfoDto();
            dto.setId(info.getId());
            dto.setFullName(info.getFullName());
            dto.setGender(info.getGender());
            dto.setPhoneNumber(info.getPhoneNumber());
            dto.setAddress(info.getAddress());
            dto.setDob(info.getDob());
            dto.setRelationship(info.getRelationship());
            return dto;
        }).toList();
        return ResponseEntity.ok(new ApiResponse("Your patient information list", dtos));
    }


    /**
     * 🟢 GET BY ID
     */
    @PreAuthorize("hasAnyAuthority('Admin','Receptionist','Patient')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse("Patient info found", patientService.getById(id)));
    }

    /**
     * 🟡 CREATE
     */
    @PreAuthorize("hasAnyAuthority('Patient','Admin')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody PatientInformation info, HttpServletRequest req) {
        Long ownerId = (Long) req.getAttribute("userId");
        PatientInformation created = patientService.create(info, ownerId);
        return ResponseEntity.ok(new ApiResponse("Patient info created", created));
    }

    /**
     * 🟠 UPDATE
     */
    @PreAuthorize("hasAnyAuthority('Patient','Admin')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody PatientInformation body) {
        return ResponseEntity.ok(new ApiResponse("Patient info updated", patientService.update(id, body)));
    }

    /**
     * 🔴 DELETE
     */
    @PreAuthorize("hasAnyAuthority('Admin','Patient')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(new ApiResponse("Patient info deleted", null));
    }
}
