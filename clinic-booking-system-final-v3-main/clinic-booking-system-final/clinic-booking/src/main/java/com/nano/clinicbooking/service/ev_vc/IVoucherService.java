package com.nano.clinicbooking.service.ev_vc;

import com.nano.clinicbooking.dto.request.VoucherRequest;
import com.nano.clinicbooking.dto.response.VoucherResponse;

import java.util.List;

public interface IVoucherService {

    // 🟢 Tạo voucher mới
    VoucherResponse createVoucher(VoucherRequest request);

    // 🟡 Cập nhật voucher
    VoucherResponse updateVoucher(Long id, VoucherRequest request);

    // 🔴 Xóa mềm voucher
    void deleteVoucher(Long id);

    // 🟣 Lấy tất cả voucher đang hoạt động
    List<VoucherResponse> getAllActive();

    // 🟤 Lấy voucher theo ID
    VoucherResponse getVoucherById(Long id);
}
