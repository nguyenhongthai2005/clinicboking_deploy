package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAttributesMapper {

    private final PasswordEncoder passwordEncoder;

    public void setCommonAttributes(RegistrationRequest source, User target) {
        target.setFullName(source.getFullName());
        target.setEmail(source.getEmail());
        target.setPassword(passwordEncoder.encode(source.getPassword())); // 🔐 mã hoá
        target.setPhoneNumber(source.getPhoneNumber());
        target.setGender(source.getGender());
        target.setUserType(source.getUserType());
    }
}
