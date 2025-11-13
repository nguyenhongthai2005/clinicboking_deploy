package com.nano.clinicbooking.repository.event_voucher;

import com.nano.clinicbooking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsEnabledTrue();

}
