package com.nano.clinicbooking.service.email.send_event;

import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.repository.patient.PatientRepository;
import com.nano.clinicbooking.service.email.send_register.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAnnouncer {

    private final EmailService emailService;
    private final EventEmailContentService eventContent;
    private final PatientRepository patientRepo;

    /**
     * Gửi email thông báo sự kiện cho toàn bộ bệnh nhân đang hoạt động.
     */
    @Async // có thể bỏ nếu muốn chạy đồng bộ
    public void announce(Event ev) {
        if (ev == null || !ev.isEnabled()) {
            throw new IllegalArgumentException("Thiếu hoặc sự kiện không khả dụng.");
        }

        final int pageSize = 500;
        Pageable pageable = PageRequest.of(0, pageSize);

        final String subject = "Sự kiện: " + ev.getTitle();
        final String ctaText = "Xem chi tiết sự kiện";
        final String ctaUrl  = null;

        while (true) {
            Slice<Patient> slice = patientRepo.findByIsEnableTrueAndEmailIsNotNull(pageable);

            slice.getContent().forEach(p -> {
                try {
                    String html = eventContent.buildEventEmailHtml(p, ev, subject, ctaText, ctaUrl);
                    emailService.sendToUser(p, subject, html, true);
                    log.info(" Đã gửi email sự kiện tới {} ({})", p.getFullName(), p.getEmail());
                } catch (Exception ex) {
                    log.error("Gửi email tới {} lỗi: {}", p.getEmail(), ex.getMessage());
                }
            });

            if (!slice.hasNext()) break;
            pageable = slice.nextPageable();
        }
    }
}
