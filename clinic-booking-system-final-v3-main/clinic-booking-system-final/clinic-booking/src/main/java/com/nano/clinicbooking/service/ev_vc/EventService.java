package com.nano.clinicbooking.service.ev_vc;

import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.model.Event;
import com.nano.clinicbooking.model.Voucher;
import com.nano.clinicbooking.repository.event_voucher.EventRepository;
import com.nano.clinicbooking.repository.event_voucher.VoucherRepository;
import com.nano.clinicbooking.dto.request.EventRequest;
import com.nano.clinicbooking.dto.response.EventResponse;
import com.nano.clinicbooking.service.email.send_event.EventAnnouncer;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final VoucherRepository voucherRepository;
    private final EventAnnouncer eventAnnouncer; // thêm dòng này de gui event ve mail
    private final EntityConverter<Event, EventResponse> converter;
    private final EntityManager entityManager;

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setEnabled(true);

        if (request.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher ID: " + request.getVoucherId()));
            event.setVoucher(voucher);
        }

        Event savedEvent = eventRepository.save(event);
// 🟢 THÊM KHỐI NÀY — gọi EventAnnouncer để gửi email broadcast
        try {
            eventAnnouncer.announce(savedEvent);
        } catch (Exception ex) {
            System.err.println(" Lỗi gửi email thông báo sự kiện: " + ex.getMessage());
        }
        // ✅ ModelMapper sẽ tự map cả VoucherResponse
        return converter.mapEntityToDto(savedEvent, EventResponse.class);
    }

    // ✅ Cập nhật event
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setEnabled(request.getIsEnabled());

        if (request.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + request.getVoucherId()));
            event.setVoucher(voucher);
        } else {
            event.setVoucher(null);
        }

        Event updated = eventRepository.save(event);
        EventResponse response = converter.mapEntityToDto(updated, EventResponse.class);
        //moi them
//  THÊM KHỐI NÀY — nếu event vẫn bật, gửi email lại cho bệnh nhân
        if (updated.isEnabled()) {
            eventAnnouncer.announce(updated);
        }
        if (updated.getVoucher() != null) {
            response.setVoucherId(updated.getVoucher().getId());
            response.setVoucherCode(updated.getVoucher().getCode());
            response.setVoucherName(updated.getVoucher().getName());
        }

        return response;
    }

    // ✅ Xóa mềm event
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setEnabled(false);
        eventRepository.save(event);
    }

    // ✅ Lấy danh sách event (chỉ hiển thị enable = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findByIsEnabledTrue()
                .stream()
                .map(event -> {
                    EventResponse response = converter.mapEntityToDto(event, EventResponse.class);
                    if (event.getVoucher() != null) {
                        response.setVoucherId(event.getVoucher().getId());
                        response.setVoucherCode(event.getVoucher().getCode());
                        response.setVoucherName(event.getVoucher().getName());
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        EventResponse response = converter.mapEntityToDto(event, EventResponse.class);
        if (event.getVoucher() != null) {
            response.setVoucherId(event.getVoucher().getId());
            response.setVoucherCode(event.getVoucher().getCode());
            response.setVoucherName(event.getVoucher().getName());
        }
        return response;
    }
}
