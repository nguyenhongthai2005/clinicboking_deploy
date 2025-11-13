package com.nano.clinicbooking.enums;

public enum AppointmentStatus {
    PENDING_CONFIRMATION, // bệnh nhân đặt, chờ duyệt
    CONFIRMED,            // đã xác nhận
    CHECKED_IN,           // bệnh nhân đã đến
    IN_PROGRESS,          // đang khám
    COMPLETED,            // đã hoàn tất
    CANCELLED,            // hủy bởi lễ tân/bệnh nhân
    NO_SHOW,              // không đến
    RESCHEDULED,          // dời lịch
    EXPIRED               // hết ca chưa khám
}
//Thời điểm	Hành động	Trạng thái
//Bệnh nhân đặt lịch	→ PENDING_CONFIRMATION
//Lễ tân duyệt	→ CONFIRMED
//Đến nơi	→ CHECKED_IN
//Bắt đầu khám	→ IN_PROGRESS
//Kết thúc	→ COMPLETED
//Hết ca mà chưa check-in	→ NO_SHOW (cron tự động)
//Hết ca mà chưa confirm	→ EXPIRED (cron tự động)
//Bệnh nhân hủy	→ CANCELLED (patient API)