package com.nano.clinicbooking.service.appointment;

import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.model.Appointment;
import com.nano.clinicbooking.repository.appointment.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentSchedulerService {

    private final AppointmentRepository appointmentRepo;

    /**
     * 🕒 Cron chạy mỗi 15 phút
     * Kiểm tra và cập nhật:
     *  - Cuộc hẹn CONFIRMED nhưng chưa CHECKED_IN → NO_SHOW
     *  - Cuộc hẹn PENDING_CONFIRMATION mà hết ca → EXPIRED
     */
    @Scheduled(cron = "0 */15 * * * *") // mỗi 15 phút
    @Transactional
    public void autoUpdateAppointmentStatuses() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        System.out.println("🔄 [CRON] Auto update appointments running at " + now);

        // ✅ 1. CONFIRMED nhưng chưa checkin → NO_SHOW
        List<Appointment> confirmedList = appointmentRepo.findAllByAppointmentDateAndStatus(today, AppointmentStatus.CONFIRMED);
        for (Appointment app : confirmedList) {
            if (Boolean.FALSE.equals(app.getCheckedIn())
                    && app.getShift() != null
                    && app.getShift().getEndTime().isBefore(now)) {
                app.setStatus(AppointmentStatus.NO_SHOW);
                appointmentRepo.save(app);
                System.out.println("→ Marked NO_SHOW: " + app.getId());
            }
        }

        // ✅ 2. PENDING_CONFIRMATION mà hết ca → EXPIRED
        List<Appointment> pendingList = appointmentRepo.findAllByAppointmentDateAndStatus(today, AppointmentStatus.PENDING_CONFIRMATION);
        for (Appointment app : pendingList) {
            if (app.getShift() != null
                    && app.getShift().getEndTime().isBefore(now)) {
                app.setStatus(AppointmentStatus.EXPIRED);
                appointmentRepo.save(app);
                System.out.println("→ Marked EXPIRED: " + app.getId());
            }
        }
    }
}
