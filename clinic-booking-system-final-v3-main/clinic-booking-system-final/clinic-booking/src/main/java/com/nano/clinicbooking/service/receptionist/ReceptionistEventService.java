package com.nano.clinicbooking.service.receptionist;

import com.nano.clinicbooking.enums.UserEventStatus;
import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.model.UserEvent;
import com.nano.clinicbooking.model.UserVoucher;
import com.nano.clinicbooking.repository.event_voucher.EventRepository;
import com.nano.clinicbooking.repository.event_voucher.UserEventRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.repository.event_voucher.UserVoucherRepository;
import com.nano.clinicbooking.dto.response.UserEventResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceptionistEventService {

    private final UserEventRepository userEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserVoucherRepository userVoucherRepository;

    @Transactional
    public UserEventResponse confirmUserAttendance(Long eventId, Long userId) {
        // 🔍 Tìm bản ghi user_event
        UserEvent userEvent = userEventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng đăng ký sự kiện này"));

        // 🔒 Kiểm tra đã xác nhận chưa
        if (userEvent.getStatus() == UserEventStatus.CONFIRMED) {
            throw new RuntimeException("Người dùng đã được xác nhận trước đó");
        }

        // ✅ Cập nhật trạng thái
        userEvent.setStatus(UserEventStatus.CONFIRMED);
        userEventRepository.save(userEvent);

        // ✅ Nếu event có voucher, thì cấp cho user (tạo bản ghi trong user_vouchers)
        Event event = userEvent.getEvent();
        if (event.getVoucher() != null) {

            // ⚙️ Kiểm tra user đã có voucher này chưa
            boolean exists = userVoucherRepository.existsByUserIdAndVoucherId(userId, event.getVoucher().getId());
            if (!exists) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

                UserVoucher userVoucher = UserVoucher.builder()
                        .user(user)
                        .voucher(event.getVoucher())
                        .used(false)
                        .build();

                userVoucherRepository.save(userVoucher);
            }
        }

        // 🧾 Trả về kết quả
        return UserEventResponse.builder()
                .id(userEvent.getId())
                .userId(userId)
                .eventId(eventId)
                .status(userEvent.getStatus().name())
                .registeredAt(userEvent.getRegisteredAt())
                .build();
    }
}
