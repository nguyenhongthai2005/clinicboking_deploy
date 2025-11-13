package com.nano.clinicbooking.controller.recep;

import com.nano.clinicbooking.dto.response.UserSearchResponse;
import com.nano.clinicbooking.dto.response.UserVoucherResponse;
import com.nano.clinicbooking.service.receptionist.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserSearchController {

    private final UserSearchService userSearchService;

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('Receptionist')")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(userSearchService.searchUsers(keyword));
    }


    // ✅ Lễ tân xem tất cả voucher của user
    @GetMapping("/{id}/vouchers")
    @PreAuthorize("hasAuthority('Receptionist')")
    public ResponseEntity<List<UserVoucherResponse>> getUserVouchers(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userSearchService.getVouchersOfUser(userId));
    }
}
