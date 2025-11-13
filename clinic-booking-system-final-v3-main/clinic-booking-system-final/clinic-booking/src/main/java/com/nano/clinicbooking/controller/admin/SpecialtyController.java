package com.nano.clinicbooking.controller.admin;

import com.nano.clinicbooking.model.Specialty;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll() {
        List<Specialty> list = specialtyRepository.findAll();
        return ResponseEntity.ok(new ApiResponse("All specialties", list));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody Specialty s) {
        Specialty newSpec = specialtyRepository.save(s);
        return ResponseEntity.ok(new ApiResponse("Specialty created", newSpec));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody Specialty s) {
        Specialty exist = specialtyRepository.findById(id).orElseThrow();
        exist.setName(s.getName());
        exist.setDescription(s.getDescription());
        specialtyRepository.save(exist);
        return ResponseEntity.ok(new ApiResponse("Specialty updated", exist));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        Specialty exist = specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialty not found"));

        // Kiểm tra xem có bác sĩ nào đang thuộc chuyên khoa này không
        long doctorCount = doctorRepository.countBySpecialtyId(id);
        if (doctorCount > 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Cannot delete: There are doctors under this specialty", null));
        }

        specialtyRepository.delete(exist);
        return ResponseEntity.ok(new ApiResponse("Specialty deleted successfully", null));
    }
}
