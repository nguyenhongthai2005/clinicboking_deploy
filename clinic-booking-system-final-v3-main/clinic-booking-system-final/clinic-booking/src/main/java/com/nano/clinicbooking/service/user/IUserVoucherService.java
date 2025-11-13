package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.dto.response.UserVoucherResponse;

import java.util.List;

public interface IUserVoucherService {

    List<UserVoucherResponse> getMyVouchers(Long userId);

    UserVoucherResponse useVoucher(Long userId, Long voucherId);


}
