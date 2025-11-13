package com.nano.clinicbooking.factory;

import com.nano.clinicbooking.model.Admin;
import com.nano.clinicbooking.repository.AdminRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;


    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }
}
