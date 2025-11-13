package com.nano.clinicbooking.repository.shiftSlot;

import com.nano.clinicbooking.enums.ShiftCancelStatus;
import com.nano.clinicbooking.model.ShiftCancelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftCancelRequestRepository extends JpaRepository<ShiftCancelRequest, Long> {
    boolean existsByShiftIdAndStatus(Long shiftId, ShiftCancelStatus status);
}
