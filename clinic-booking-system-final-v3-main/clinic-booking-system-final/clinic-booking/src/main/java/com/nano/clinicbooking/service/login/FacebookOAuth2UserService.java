package com.nano.clinicbooking.service.login;

import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.patient.PatientRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.service.email.send_register.AiEmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.Map;

@Service  // ✅ Quan trọng: thêm annotation này
@RequiredArgsConstructor
public class FacebookOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AiEmailService aiEmailService;
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(req);

        // Chỉ xử lý khi provider là "facebook"
        if (!"facebook".equalsIgnoreCase(req.getClientRegistration().getRegistrationId())) {
            return oAuth2User;
        }

        Map<String, Object> attrs = oAuth2User.getAttributes();
        String email = (String) attrs.get("email");
        String name  = (String) attrs.get("name");

        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Facebook account has no email");
        }
        if (name == null || name.isBlank()) {
            name = email;
        }
        //        fix lai ngay 29/10/2025
        boolean isFirstLogin = false;
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Lần đầu → tạo Patient (map vào bảng users + patient)
            Patient p = new Patient();
            p.setEmail(email);
            p.setPassword("{noop}FACEBOOK_LOGIN");
            p.setUserType("Patient");
            p.setIsEnable(true);
            p.setFullName(name);
            user = patientRepository.save(p);
            isFirstLogin = true;
        } else {
            // Đã tồn tại → cập nhật thông tin cơ bản
            user.setFullName(name);
            user.setUserType("Patient");
            user.setIsEnable(true);
            userRepository.save(user);
        }
        // Gửi email chào mừng SAU KHI COMMIT (chỉ lần đầu)
        if (isFirstLogin) {
            final User u = user;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        aiEmailService.sendRegisterSuccessEmail(u);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "id"
        );
    }
}
