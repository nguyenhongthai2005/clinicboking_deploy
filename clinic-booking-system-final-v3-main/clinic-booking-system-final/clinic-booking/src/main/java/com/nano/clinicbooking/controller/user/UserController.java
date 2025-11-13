package com.nano.clinicbooking.controller.user;

import com.nano.clinicbooking.dto.request.UserUpdateRequest;
import com.nano.clinicbooking.dto.response.ApiResponse;
import com.nano.clinicbooking.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('Admin','Doctor','Receptionist','Patient')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getProfile(HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        return ResponseEntity.ok(
                new ApiResponse("Profile found", userService.findById(userId))
        );
    }

    @PreAuthorize("hasAuthority('Patient')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest req, @RequestBody UserUpdateRequest body) {
        Long userId = (Long) req.getAttribute("userId");
        String role = (String) req.getAttribute("role"); // -> Patient
        return ResponseEntity.ok(
                new ApiResponse("User updated successfully", userService.update(userId, role, body))
        );
    }

}
