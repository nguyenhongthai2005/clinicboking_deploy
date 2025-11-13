package com.nano.clinicbooking.service.email.sent_appointment;

import com.nano.clinicbooking.dto.response.AppointmentDto;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import com.nano.clinicbooking.service.email.send_register.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentAnnouncer {

    private final EmailService emailService;
    private final AppointmentEmailContentService appointmentContent;
    private final AppointmentRepository appointmentRepo;

    /**
     * Gửi email xác nhận cho một lịch hẹn cụ thể (gọi ngay sau khi tạo)
     */
    @Transactional
    public void announceAppointment(AppointmentDto dto) {
        if (dto == null) {
            log.warn("⚠️ Không thể gửi email vì AppointmentDto = null");
            return;
        }

        try {
            // ⚙️ Lấy đầy đủ dữ liệu appointment + patient (tránh lỗi LazyInitialization)
            Appointment app = appointmentRepo.findById(dto.getId()).orElse(null);
            if (app == null) {
                log.warn("⚠️ Appointment {} không tồn tại trong DB", dto.getId());
                return;
            }

            User patient = app.getPatient();
            if (patient == null || patient.getEmail() == null || patient.getEmail().isBlank()) {
                log.warn("⚠️ Appointment {} không có email bệnh nhân", dto.getId());
                return;
            }

            // 📨 Tiêu đề email
            String subject = "📅 Xác nhận lịch hẹn khám bệnh ngày "
                    + dto.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // 🧩 Sinh nội dung HTML
            String html = appointmentContent.buildAppointmentEmailHtml(patient, dto, subject);

            // 🚀 Gửi mail
            emailService.sendToUser(patient, subject, html, true);
            log.info("✅ Đã gửi email lịch hẹn tới {} ({})", patient.getFullName(), patient.getEmail());

        } catch (Exception ex) {
            log.error("❌ Gửi email cho appointment {} lỗi: {}", dto.getId(), ex.getMessage(), ex);
        }
    }
}
