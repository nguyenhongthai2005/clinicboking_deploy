package com.nano.clinicbooking.repository.patient_info;

import com.nano.clinicbooking.model.PatientInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientInformationRepository extends JpaRepository<PatientInformation,Long> {
    List<PatientInformation> findByOwnerId(Long ownerId);
}
