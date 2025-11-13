package com.nano.clinicbooking.service.email.send_event;

import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.User;

public interface EventEmailContentProvider {
    String buildEventEmailHtml(User user, Event ev, String subject, String ctaText, String ctaUrl);
    // ... sinh HTML bằng Gemini + fallback template ...
}
