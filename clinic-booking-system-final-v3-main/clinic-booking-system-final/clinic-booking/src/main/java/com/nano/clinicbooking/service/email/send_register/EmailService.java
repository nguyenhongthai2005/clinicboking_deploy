package com.nano.clinicbooking.service.email.send_register;

import com.nano.clinicbooking.model.User;

public interface EmailService {
    void sendToUser(User user, String subject, String content, boolean isHtml);

}
