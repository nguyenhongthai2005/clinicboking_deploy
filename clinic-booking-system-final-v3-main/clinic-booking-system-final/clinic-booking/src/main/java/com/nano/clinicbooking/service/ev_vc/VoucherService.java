package com.nano.clinicbooking.service.ev_vc;

import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.model.Voucher;
import com.nano.clinicbooking.repository.event_voucher.VoucherRepository;
import com.nano.clinicbooking.dto.request.VoucherRequest;
import com.nano.clinicbooking.dto.response.VoucherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService implements IVoucherService {

    private final VoucherRepository voucherRepository;
    private final EntityConverter<Voucher, VoucherResponse> converter;

    // 🟢 Tạo voucher mới
    public VoucherResponse createVoucher(VoucherRequest request) {
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setName(request.getName());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setType(request.getType());
        voucher.setEnable(request.getEnable());
        voucher.setExpiryDate(request.getExpiryDate());

        Voucher saved = voucherRepository.save(voucher);
        return converter.mapEntityToDto(saved, VoucherResponse.class);
    }

    // 🟢 Lấy tất cả voucher đang hoạt động
    public List<VoucherResponse> getAllActive() {
        return voucherRepository.findAllByEnableTrue()
                .stream()
                .map(v -> converter.mapEntityToDto(v, VoucherResponse.class))
                .toList();
    }

    @Override
    public VoucherResponse getVoucherById(Long id) {
        return null;
    }

    // 🟡 Cập nhật voucher
    public VoucherResponse updateVoucher(Long id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        voucher.setName(request.getName());
        voucher.setCode(request.getCode());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setType(request.getType());
        voucher.setExpiryDate(request.getExpiryDate());
        voucher.setEnable(request.getEnable());

        Voucher updated = voucherRepository.save(voucher);
        return converter.mapEntityToDto(updated, VoucherResponse.class);
    }

    // 🔴 Xóa mềm voucher
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucher.setEnable(false);
        voucherRepository.save(voucher);
    }
}
