package com.nano.clinicbooking.controller;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.model.Receptionist;
import com.nano.clinicbooking.repository.receptionist.ReceptionistRepository;
import com.nano.clinicbooking.dto.request.UserUpdateRequest;
import com.nano.clinicbooking.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {

    private final ReceptionistRepository receptionistRepo;

    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllReceptionists() {
        List<Receptionist> list = receptionistRepo.findAll();
        List<UserDto> dtos = list.stream().map(r -> {
            UserDto dto = new UserDto();
            dto.setId(r.getId());
            dto.setFullName(r.getFullName());
            dto.setEmail(r.getEmail());
            dto.setPhoneNumber(r.getPhoneNumber());
            dto.setGender(r.getGender());
            return dto;
        }).toList();
        return ResponseEntity.ok(new ApiResponse("All receptionists", dtos));
    }

    @PreAuthorize("hasAnyAuthority('Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getReceptionist(@PathVariable Long id) {
        Receptionist r = receptionistRepo.findById(id).orElseThrow(() -> new RuntimeException("Receptionist not found"));
        UserDto dto = new UserDto();
        dto.setId(r.getId());
        dto.setFullName(r.getFullName());
        dto.setEmail(r.getEmail());
        dto.setPhoneNumber(r.getPhoneNumber());
        dto.setGender(r.getGender());
        return ResponseEntity.ok(new ApiResponse("Receptionist detail", dto));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateReceptionist(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        Receptionist receptionist = receptionistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Receptionist not found"));

        if (req.getFullName() != null) receptionist.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) receptionist.setPhoneNumber(req.getPhoneNumber());
        if (req.getGender() != null) receptionist.setGender(req.getGender());

        receptionistRepo.save(receptionist);
        return ResponseEntity.ok(new ApiResponse("Receptionist updated successfully", receptionist));
    }
}
