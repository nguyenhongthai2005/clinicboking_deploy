package com.nano.clinicbooking.controller.voucher;

import com.nano.clinicbooking.dto.response.UserVoucherResponse;
import com.nano.clinicbooking.service.user.UserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class UserVoucherController {

    private final UserVoucherService userVoucherService;

    // ✅ User xem danh sách voucher của mình
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('Patient')")
    public ResponseEntity<List<UserVoucherResponse>> getMyVouchers(Principal principal) {
        Long userId = Long.parseLong(principal.getName()); // Lấy userId từ token
        return ResponseEntity.ok(userVoucherService.getMyVouchers(userId));
    }

    // ✅ Lễ tân xác nhận voucher đã được dùng
    @PostMapping("/{id}/use")
    @PreAuthorize("hasAuthority('Receptionist')")
    public ResponseEntity<UserVoucherResponse> useVoucher(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(userVoucherService.useVoucher(userId, id));
    }

    // ✅ Lễ tân xem danh sách voucher của một user cụ thể
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('Receptionist')")
    public ResponseEntity<List<UserVoucherResponse>> getVouchersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userVoucherService.getMyVouchers(userId));
    }


}
