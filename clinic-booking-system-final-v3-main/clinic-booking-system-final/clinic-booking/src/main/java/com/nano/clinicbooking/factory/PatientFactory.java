package com.nano.clinicbooking.factory;

import com.nano.clinicbooking.model.Patient;
import com.nano.clinicbooking.repository.patient.PatientRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientFactory {
    private final PatientRepository patientRepository;
    private final UserAttributesMapper userAttributesMapper;

    public Patient createPatient(RegistrationRequest request) {
        Patient patient = new Patient();
        userAttributesMapper.setCommonAttributes(request, patient);
        return patientRepository.save(patient);
    }
}
