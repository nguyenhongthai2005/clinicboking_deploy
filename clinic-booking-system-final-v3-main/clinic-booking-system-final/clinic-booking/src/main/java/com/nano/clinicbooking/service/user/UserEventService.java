package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.enums.UserEventStatus;
import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.model.UserEvent;
import com.nano.clinicbooking.repository.event_voucher.EventRepository;
import com.nano.clinicbooking.repository.event_voucher.UserEventRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.dto.response.UserEventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserEventRepository userEventRepository;
    private final EntityConverter converter;

    public UserEventResponse registerUserForEvent(Long userId, Long eventId) {
        // 🔒 Kiểm tra xem user đã đăng ký chưa
        if (userEventRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Bạn đã đăng ký sự kiện này rồi!");
        }

        // 🔍 Tìm user và event
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        // 📝 Tạo bản ghi mới
        UserEvent userEvent = UserEvent.builder()
                .user(user)
                .event(event)
                .registeredAt(LocalDateTime.now())
                .status(UserEventStatus.PENDING)
                .build();

        userEventRepository.save(userEvent);

        // 🧭 Trả về response
        return UserEventResponse.builder()
                .id(userEvent.getId())
                .userId(user.getId())
                .eventId(event.getId())
                .eventName(event.getTitle())
                .registeredAt(userEvent.getRegisteredAt())
                .status(userEvent.getStatus().name())
                .build();
    }
}
