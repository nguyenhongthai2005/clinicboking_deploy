package com.nano.clinicbooking.controller.admin;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.Receptionist;
import com.nano.clinicbooking.model.Specialty;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.receptionist.ReceptionistRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.dto.request.UserUpdateRequest;
import com.nano.clinicbooking.dto.response.ApiResponse;
import com.nano.clinicbooking.service.doctor.DoctorService;
import com.nano.clinicbooking.service.patient.PatientService;
import com.nano.clinicbooking.service.receptionist.ReceptionistService;
import com.nano.clinicbooking.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final DoctorRepository doctorRepo;
    private final ReceptionistRepository receptionistRepo;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final ReceptionistService receptionistService;
    private final SpecialtyRepository specialtyRepo;

    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse> registerAdmin(@RequestBody RegistrationRequest req) {
        req.setUserType("Admin");
        User admin = userService.register(req); // qua UserFactory -> AdminFactory -> adminRepo.save
        return ResponseEntity.ok(new ApiResponse("Admin registered successfully", admin));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse> createUser(@RequestBody RegistrationRequest request) {
        var user = userService.register(request);
        return ResponseEntity.ok(new ApiResponse("User created successfully by admin", user));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse("All users", users));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse("User deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new ApiResponse("User details", user));
    }


    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/update-doctor/{id}")
    public ResponseEntity<ApiResponse> updateDoctor(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (req.getFullName() != null) doctor.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) doctor.setPhoneNumber(req.getPhoneNumber());
        if (req.getGender() != null) doctor.setGender(req.getGender());
        if (req.getDegree() != null) doctor.setDegree(req.getDegree());
        if (req.getExperience() != null) doctor.setExperience(req.getExperience());
        if (req.getDescription() != null) doctor.setDescription(req.getDescription());

        // Update specialty if provided
        if (req.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepo.findById(req.getSpecialtyId())
                    .orElseThrow(() -> new RuntimeException("Specialty not found"));
            doctor.setSpecialty(specialty);
        }
        doctorRepo.save(doctor);
        return ResponseEntity.ok(new ApiResponse("Doctor updated successfully", doctor));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/update-receptionist/{id}")
    public ResponseEntity<ApiResponse> updateReceptionist(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        Receptionist receptionist = receptionistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Receptionist not found"));

        if (req.getFullName() != null) receptionist.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) receptionist.setPhoneNumber(req.getPhoneNumber());
        if (req.getGender() != null) receptionist.setGender(req.getGender());

        receptionistRepo.save(receptionist);
        return ResponseEntity.ok(new ApiResponse("Receptionist updated successfully", receptionist));
    }

    // =========================
    // 🔹 HIỂN THỊ DANH SÁCH CHO ADMIN
    // =========================


    //ai cũng xem đc tất cả danh sách doctor
    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse> getAllDoctors() {
        List<UserDto> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(new ApiResponse("List of doctors", doctors));
    }

    //đã có 1 cái getAll patient trong /api/v1/patients-info/all
    // api trên là : Medical record PatientInformation (hồ sơ khám hoặc người được khám)

    //còn /api/v1/admin/patients là lấy người dùng đki tài khoản
    //User-level Patient (người dùng bệnh nhân đăng ký tài khoản)
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/patients")
    public ResponseEntity<ApiResponse> getAllPatients() {
        List<UserDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(new ApiResponse("List of patients", patients));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/receptionists")
    public ResponseEntity<ApiResponse> getAllReceptionists() {
        List<UserDto> receptionists = receptionistService.getAllReceptionists();
        return ResponseEntity.ok(new ApiResponse("List of receptionists", receptionists));
    }

    // =========================
    // ACTIVE tài khoản
    // =========================

    // API để xem tất cả user, kể cả đã khoá (Admin)
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/all-including-disabled")
    public ResponseEntity<ApiResponse> getAllUsersIncludingDisabled() {
        List<UserDto> users = userService.getAllUsersIncludingDisabled();
        return ResponseEntity.ok(new ApiResponse("All users including disabled", users));
    }

    // API để kích hoạt lại user (Admin)
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/activate/{id}")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        userService.activateUserById(id);
        return ResponseEntity.ok(new ApiResponse("User activated successfully", null));
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/disabled")
    public ResponseEntity<List<UserDto>> getDisabledUsers() {
        return ResponseEntity.ok(userService.getAllDisabledUsers());
    }

}
