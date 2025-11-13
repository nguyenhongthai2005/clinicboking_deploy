package com.nano.clinicbooking.service.email.send_register;

import com.nano.clinicbooking.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String defaultFrom;

    @Override
    public void sendToUser(User user, String subject, String content, boolean isHtml) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Không có thông tin email người nhận");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(defaultFrom);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            mailSender.send(message);
            System.out.println("Đã gửi email tới: " + user.getEmail());

        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }
}
