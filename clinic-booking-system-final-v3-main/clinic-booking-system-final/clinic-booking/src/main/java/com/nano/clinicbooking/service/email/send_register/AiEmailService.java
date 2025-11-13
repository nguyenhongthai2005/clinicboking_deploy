package com.nano.clinicbooking.service.email.send_register;

import com.nano.clinicbooking.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiEmailService {

    private final EmailService emailService;
    private final RegisterEmailContentService registerContent;

    // Gửi email đăng ký thành công, AI tự viết nội dung
    public void sendRegisterSuccessEmail(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException(" Thiếu thông tin người nhận email");
        }

        String subject = " Đăng ký thành công - ClinicBooking";
        String html = registerContent.generateRegisterSuccessEmail(user);

        emailService.sendToUser(user, subject, html, true);
        System.out.printf(" Đã gửi email đăng ký thành công cho %s (%s)%n", user.getFullName(), user.getEmail());
    }
}
