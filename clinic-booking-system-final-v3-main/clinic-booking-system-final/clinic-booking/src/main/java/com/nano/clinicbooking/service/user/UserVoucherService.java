package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.model.UserVoucher;
import com.nano.clinicbooking.repository.event_voucher.UserVoucherRepository;
import com.nano.clinicbooking.dto.response.UserVoucherResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserVoucherService implements IUserVoucherService {

    private final UserVoucherRepository userVoucherRepository;

    // ✅ Lấy danh sách voucher của user
    @Override
    @Transactional(readOnly = true)
    public List<UserVoucherResponse> getMyVouchers(Long userId) {
        return userVoucherRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Đánh dấu voucher đã sử dụng
    @Override
    @Transactional
    public UserVoucherResponse useVoucher(Long userId, Long voucherId) {
        UserVoucher userVoucher = userVoucherRepository.findByIdAndUserId(voucherId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher cho user"));

        if (userVoucher.getUsed()) {
            throw new RuntimeException("Voucher này đã được sử dụng!");
        }

        userVoucher.setUsed(true);
        userVoucherRepository.save(userVoucher);

        return mapToResponse(userVoucher);
    }

    // ✅ Convert entity → response
    private UserVoucherResponse mapToResponse(UserVoucher uv) {
        return UserVoucherResponse.builder()
                .id(uv.getId())
                .voucherId(uv.getVoucher().getId())
                .voucherCode(uv.getVoucher().getCode())
                .voucherName(uv.getVoucher().getName())
                .discountValue(uv.getVoucher().getDiscountValue())
                .type(uv.getVoucher().getType())
                .expiryDate(uv.getVoucher().getExpiryDate())
                .issuedAt(uv.getIssuedAt())
                .used(uv.getUsed())
                .build();
    }
}
