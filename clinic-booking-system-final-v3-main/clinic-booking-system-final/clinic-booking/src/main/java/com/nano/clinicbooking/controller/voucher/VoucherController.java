package com.nano.clinicbooking.controller.voucher;

import com.nano.clinicbooking.dto.request.VoucherRequest;
import com.nano.clinicbooking.dto.response.VoucherResponse;
import com.nano.clinicbooking.service.ev_vc.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // ✅ Admin: tạo voucher mới
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping("/create")
    public ResponseEntity<VoucherResponse> createVoucher(@RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.createVoucher(request);
        return ResponseEntity.ok(response);
    }

    // ✅ Admin: cập nhật voucher
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/update/{id}")
    public ResponseEntity<VoucherResponse> updateVoucher(
            @PathVariable Long id,
            @RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.updateVoucher(id, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Admin: xóa mềm voucher
    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok().build();
    }

    // ✅ Public: lấy tất cả voucher đang hoạt động
    @GetMapping("/all")
    public ResponseEntity<List<VoucherResponse>> getAllActive() {
        List<VoucherResponse> vouchers = voucherService.getAllActive();
        return ResponseEntity.ok(vouchers);
    }

    // ✅ Admin: lấy chi tiết voucher theo ID
    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucherById(@PathVariable Long id) {
        VoucherResponse response = voucherService.getVoucherById(id);
        return ResponseEntity.ok(response);
    }
}
