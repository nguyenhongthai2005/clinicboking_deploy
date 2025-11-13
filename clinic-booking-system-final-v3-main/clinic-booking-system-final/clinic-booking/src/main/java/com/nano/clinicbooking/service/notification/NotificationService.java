package com.nano.clinicbooking.service.notification;

import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;

public interface NotificationService {

    // Gửi thông báo cho User khi Admin duyệt yêu cầu khám nhóm
    void notifyUserRequestApproved(GroupHealthRequest request);

    // Gửi thông báo cho từng Doctor được phân công trong nhóm
    void notifyDoctorAssigned(Long doctorId, GroupHealthRequest request);

    //gui thong bao log khi tu choi thanh cong
    void notifyUserRequestRejected(GroupHealthRequest request, String reason);

    void notifyUserResultsSent(GroupHealthRequest request, String zipFilePath);

    void notifyMentorResultsApproved(GroupHealthRequest request);
}
