package com.nano.clinicbooking.service.patientInformation;

import com.nano.clinicbooking.model.PatientInformation;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.patient_info.PatientInformationRepository;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientInformationService implements IPatientInformationService {

    private final PatientInformationRepository patientRepo;
    private final UserRepository userRepo;

    @Override
    public List<PatientInformation> getAll() {
        return patientRepo.findAll();
    }

    @Override
    public List<PatientInformation> getByOwner(Long ownerId) {
        return patientRepo.findByOwnerId(ownerId);
    }

    @Override
    public PatientInformation getById(Long id) {
        return patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient information not found"));
    }

    @Override
    public PatientInformation create(PatientInformation info, Long ownerId) {
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        info.setOwner(owner);
        return patientRepo.save(info);
    }

    @Override
    public PatientInformation update(Long id, PatientInformation body) {
        PatientInformation existing = getById(id);
        existing.setFullName(body.getFullName());
        existing.setGender(body.getGender());
        existing.setPhoneNumber(body.getPhoneNumber());
        existing.setAddress(body.getAddress());
        existing.setDob(body.getDob());
        existing.setRelationship(body.getRelationship());
        return patientRepo.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!patientRepo.existsById(id)) {
            throw new RuntimeException("Patient information not found");
        }
        patientRepo.deleteById(id);
    }
}
