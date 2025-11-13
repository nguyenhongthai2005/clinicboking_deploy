package com.nano.clinicbooking.repository.shiftSlot;

import com.nano.clinicbooking.model.ShiftSlot;
import com.nano.clinicbooking.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShiftSlotRepository extends JpaRepository<ShiftSlot, Long> {
    List<ShiftSlot> findByShiftIdOrderBySlotNumberAsc(Long shiftId);
    Optional<ShiftSlot> findFirstByShiftIdAndStatusOrderBySlotNumberAsc(Long shiftId, SlotStatus status);
}
