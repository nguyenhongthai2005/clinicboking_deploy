package com.nano.clinicbooking.service.login;

import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.patient.PatientRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.service.email.send_register.AiEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AiEmailService aiEmailService;//moi them

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest req) {
        OidcUser oidcUser = super.loadUser(req);

        String email = oidcUser.getEmail(); // hoặc: (String) oidcUser.getAttributes().get("email")
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Google account has no email");
        }

        String fullName = oidcUser.getFullName();
        if (fullName == null || fullName.isBlank()) {
            String given = oidcUser.getGivenName() == null ? "" : oidcUser.getGivenName();
            String family = oidcUser.getFamilyName() == null ? "" : oidcUser.getFamilyName();
            fullName = (given + " " + family).trim();
            if (fullName.isBlank()) fullName = email;
        }
//        fix lai ngay 29/10/2025
//  Thêm dòng này trước khi dùng trong lambda
        boolean isFirstLogin = false;
        //  Nếu user chưa có thì tạo Patient thay vì User
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // lần đầu → tạo Patient (kế thừa User)
            Patient p = new Patient();
            p.setEmail(email);
            p.setPassword("{noop}GOOGLE_LOGIN");
            p.setUserType("Patient");
            p.setIsEnable(true);
            p.setFullName(fullName);
            user = patientRepository.save(p);
            isFirstLogin = true;
        } else {
            // đã tồn tại → cập nhật thông tin cơ bản
            user.setFullName(fullName);
            user.setUserType("Patient");
            user.setIsEnable(true);
            userRepository.save(user);
        }
        // GỬI EMAIL SAU KHI TRANSACTION COMMIT (chỉ lần đầu)
        if (isFirstLogin) {
            final User u = user; // capture to lambda
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        aiEmailService.sendRegisterSuccessEmail(u);
                        System.out.println("Welcome email sent to " + u.getEmail());
                    } catch (Exception ex) {
                        // không dùng logger thì in console để còn thấy lỗi
                        ex.printStackTrace();
                    }
                }
            });
        }
            // trả về principal OIDC
            return new DefaultOidcUser(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "sub"
            );
        }
    }

