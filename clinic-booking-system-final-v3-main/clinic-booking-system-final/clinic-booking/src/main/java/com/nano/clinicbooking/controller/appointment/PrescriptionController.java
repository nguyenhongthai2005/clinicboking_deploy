package com.nano.clinicbooking.controller.appointment;

import com.nano.clinicbooking.dto.response.PrescriptionDto;
import com.nano.clinicbooking.model.Prescription;
import com.nano.clinicbooking.dto.response.ApiResponse;
import com.nano.clinicbooking.service.prescription.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // ✅ Bác sĩ ghi đơn thuốc cho cuộc hẹn
    @PreAuthorize("hasAuthority('Doctor')")
    @PostMapping("/create/{appointmentId}")
    public ResponseEntity<ApiResponse> createPrescription(
            @PathVariable Long appointmentId,
            @Valid @RequestBody Prescription body) {

        Prescription saved = prescriptionService.createPrescription(appointmentId, body);

        // 🔸 Map sang DTO tránh serialize các quan hệ lazy
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(saved.getId());
        dto.setMedicineName(saved.getMedicineName());
        dto.setDosage(saved.getDosage());
        dto.setDuration(saved.getDuration());
        dto.setInstructions(saved.getInstructions());

        return ResponseEntity.ok(new ApiResponse("Prescription created successfully", dto));
    }


    @PreAuthorize("hasAnyAuthority('Doctor','Patient','Receptionist','Admin')")
    @GetMapping("/by-appointment/{appointmentId}")
    public ResponseEntity<ApiResponse> getByAppointment(@PathVariable Long appointmentId) {
        List<Prescription> list = prescriptionService.getPrescriptionsByAppointment(appointmentId);

        // Chuyển sang DTO list
        List<PrescriptionDto> dtos = list.stream().map(p -> {
            PrescriptionDto dto = new PrescriptionDto();
            dto.setId(p.getId());
            dto.setMedicineName(p.getMedicineName());
            dto.setDosage(p.getDosage());
            dto.setDuration(p.getDuration());
            dto.setInstructions(p.getInstructions());
            return dto;
        }).toList();

        return ResponseEntity.ok(new ApiResponse("Prescriptions for appointment", dtos));
    }

    // Bác sĩ ghi nhiều đơn thuốc cho cuộc hẹn cùng lúc
    @PreAuthorize("hasAuthority('Doctor')")
    @PostMapping("/batch-create/{appointmentId}")
    public ResponseEntity<ApiResponse> createPrescriptions(
            @PathVariable Long appointmentId,
            @Valid @RequestBody List<Prescription> prescriptions) {

        List<Prescription> saved = prescriptionService.createPrescriptions(appointmentId, prescriptions);

        // 🔸 Map sang DTO list
        List<PrescriptionDto> dtos = saved.stream().map(p -> {
            PrescriptionDto dto = new PrescriptionDto();
            dto.setId(p.getId());
            dto.setMedicineName(p.getMedicineName());
            dto.setDosage(p.getDosage());
            dto.setDuration(p.getDuration());
            dto.setInstructions(p.getInstructions());
            return dto;
        }).toList();

        return ResponseEntity.ok(new ApiResponse("Prescriptions created successfully", dtos));
    }

    // ✅ Bác sĩ cập nhật prescriptions cho cuộc hẹn (chỉ khi appointment chưa completed)
    @PreAuthorize("hasAuthority('Doctor')")
    @PutMapping("/update/{appointmentId}")
    public ResponseEntity<ApiResponse> updatePrescriptions(
            @PathVariable Long appointmentId,
            @Valid @RequestBody List<Prescription> prescriptions) {

        List<Prescription> saved = prescriptionService.updatePrescriptions(appointmentId, prescriptions);

        // 🔸 Map sang DTO list
        List<PrescriptionDto> dtos = saved.stream().map(p -> {
            PrescriptionDto dto = new PrescriptionDto();
            dto.setId(p.getId());
            dto.setMedicineName(p.getMedicineName());
            dto.setDosage(p.getDosage());
            dto.setDuration(p.getDuration());
            dto.setInstructions(p.getInstructions());
            return dto;
        }).toList();

        return ResponseEntity.ok(new ApiResponse("Prescriptions updated successfully", dtos));
    }
}
