package com.nano.clinicbooking.factory;


import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.model.Doctor;
import com.nano.clinicbooking.model.Specialty;
import com.nano.clinicbooking.repository.doctor.DoctorRepository;
import com.nano.clinicbooking.repository.SpecialtyRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorFactory {
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserAttributesMapper userAttributesMapper;

    public Doctor createDoctor(RegistrationRequest req) {
        Doctor doctor = new Doctor();
        userAttributesMapper.setCommonAttributes(req, doctor);

        Specialty specialty = specialtyRepository.findByName(req.getSpecialization())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));
        doctor.setSpecialty(specialty);

        doctor.setDegree(req.getDegree());
        doctor.setDescription(req.getDescription());
        doctor.setExperience(req.getExperience());

        return doctorRepository.save(doctor);
    }
}

