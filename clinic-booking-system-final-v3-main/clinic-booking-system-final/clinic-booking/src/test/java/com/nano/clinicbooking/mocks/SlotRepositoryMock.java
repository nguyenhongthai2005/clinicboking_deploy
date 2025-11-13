package com.nano.clinicbooking.mocks;

import com.nano.clinicbooking.model.ShiftSlot;
import com.nano.clinicbooking.enums.SlotStatus;
import java.time.LocalTime;

public class SlotRepositoryMock {

    public static ShiftSlot sampleSlot() {
        ShiftSlot slot = new ShiftSlot();
        slot.setId(1L);
        slot.setSlotNumber(1);
        slot.setStartTime(LocalTime.of(8, 0));
        slot.setEndTime(LocalTime.of(9, 0));
        slot.setStatus(SlotStatus.AVAILABLE);
        return slot;
    }
}
