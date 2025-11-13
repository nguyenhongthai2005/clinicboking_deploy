package com.nano.clinicbooking.service.email.sent_appointment;


import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.model.User;

public interface AppointmentEmailContentProvider {
    String buildAppointmentEmailHtml(User patient, AppointmentDto appointment, String subject);
}

