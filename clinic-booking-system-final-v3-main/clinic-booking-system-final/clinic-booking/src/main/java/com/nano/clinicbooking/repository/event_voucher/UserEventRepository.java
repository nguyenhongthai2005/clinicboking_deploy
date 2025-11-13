package com.nano.clinicbooking.repository.event_voucher;

import com.nano.clinicbooking.model.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    List<UserEvent> findByUserId(Long userId);
    List<UserEvent> findByEventId(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    Optional<UserEvent> findByUserIdAndEventId(Long userId, Long eventId);

}
