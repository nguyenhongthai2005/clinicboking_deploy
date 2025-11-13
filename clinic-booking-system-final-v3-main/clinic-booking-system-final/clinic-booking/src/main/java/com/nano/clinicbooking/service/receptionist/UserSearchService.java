package com.nano.clinicbooking.service.receptionist;

import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.dto.response.UserSearchResponse;
import com.nano.clinicbooking.dto.response.UserVoucherResponse;
import com.nano.clinicbooking.service.user.UserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserRepository userRepository;
    private final UserVoucherService userVoucherService;

    // 🟢 Lễ tân tìm kiếm user
    public List<UserSearchResponse> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(keyword, keyword, keyword)
                .stream()
                .map(u -> UserSearchResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phone(u.getPhoneNumber())
                        .build())
                .toList();
    }

    // 🟢 Xem toàn bộ voucher của user đó
    public List<UserVoucherResponse> getVouchersOfUser(Long userId) {
        return userVoucherService.getMyVouchers(userId);
    }
}
