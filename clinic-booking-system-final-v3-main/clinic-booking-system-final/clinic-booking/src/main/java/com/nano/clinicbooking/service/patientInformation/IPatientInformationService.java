package com.nano.clinicbooking.service.patientInformation;

import com.nano.clinicbooking.model.PatientInformation;

import java.util.List;

public interface IPatientInformationService {
    public List<PatientInformation> getAll();
    public List<PatientInformation> getByOwner(Long ownerId);
    public PatientInformation getById(Long id);
    public PatientInformation create(PatientInformation info, Long ownerId);
    public PatientInformation update(Long id, PatientInformation body);
    public void delete(Long id);
}
