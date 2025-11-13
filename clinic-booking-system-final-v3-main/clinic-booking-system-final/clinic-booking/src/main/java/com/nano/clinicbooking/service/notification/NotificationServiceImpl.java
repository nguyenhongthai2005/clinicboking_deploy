package com.nano.clinicbooking.service.notification;

import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifyUserRequestApproved(GroupHealthRequest request) {
        // Bạn có thể thay thế log này bằng gửi email hoặc push notification sau
        log.info("[NOTIFY][USER] ✅ Yêu cầu khám nhóm '{}' (ID={}) đã được duyệt. "
                        + "Ngày khám: {} | SĐT liên hệ: {}",
                request.getGroupName(),
                request.getId(),
                request.getPreferredDate(),
                request.getPhoneNumber());
    }

    @Override
    public void notifyDoctorAssigned(Long doctorId, GroupHealthRequest request) {
        log.info("[NOTIFY][DOCTOR:{}] 👨‍⚕️ Bạn được phân công khám nhóm '{}' vào ngày {}.",
                doctorId,
                request.getGroupName(),
                request.getPreferredDate());
    }

    public void notifyUserRequestRejected(GroupHealthRequest req, String reason) {
        log.info("🚫 Group health request [{}] REJECTED for {}. Reason: {}", req.getId(), req.getGroupName(), reason);
    }


    // 🆕 Thông báo log khi admin duyệt kết quả và hoàn tất gửi
    public void notifyUserResultsSent(GroupHealthRequest request, String zipFilePath) {
        log.info("[NOTIFY][CUSTOMER] 📩 Kết quả khám nhóm '{}' (ID={}) đã được tổng hợp "
                        + "và gửi thành công. File ZIP: {}",
                request.getGroupName(),
                request.getId(),
                zipFilePath);
    }

    // (Tuỳ chọn) Thông báo nội bộ cho Mentor/Admin khi đã gửi khách hàng
    public void notifyMentorResultsApproved(GroupHealthRequest request) {
        log.info("[NOTIFY][MENTOR] 🧾 Kết quả nhóm '{}' (ID={}) đã được admin duyệt và gửi cho khách hàng.",
                request.getGroupName(),
                request.getId());
    }



}
