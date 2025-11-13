package com.nano.clinicbooking.repository.event_voucher;

import com.nano.clinicbooking.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findAllByEnableTrue();
}
