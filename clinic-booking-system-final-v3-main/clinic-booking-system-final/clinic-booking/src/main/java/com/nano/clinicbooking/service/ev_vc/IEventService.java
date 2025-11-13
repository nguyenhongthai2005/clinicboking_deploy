package com.nano.clinicbooking.service.ev_vc;

import com.nano.clinicbooking.dto.request.EventRequest;
import com.nano.clinicbooking.dto.response.EventResponse;

import java.util.List;

public interface IEventService {

    // 🟢 Tạo mới một event
    EventResponse createEvent(EventRequest request);

    // 🟡 Cập nhật event
    EventResponse updateEvent(Long id, EventRequest request);

    // 🔴 Xóa mềm event
    void deleteEvent(Long id);

    // 🟣 Lấy tất cả event đang hoạt động
    List<EventResponse> getAllEvents();

    // 🟤 Lấy event theo id (tuỳ chọn)
    EventResponse getEventById(Long id);
}
