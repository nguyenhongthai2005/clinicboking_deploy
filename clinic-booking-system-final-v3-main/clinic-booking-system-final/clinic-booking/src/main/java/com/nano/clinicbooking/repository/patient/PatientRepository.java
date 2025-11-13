package com.nano.clinicbooking.repository.patient;

import com.nano.clinicbooking.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    //moi them
    Slice<Patient> findByIsEnableTrueAndEmailIsNotNull(Pageable pageable);
}
