package com.nano.clinicbooking.factory;


import com.nano.clinicbooking.exception.UserAlreadyExitsException;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFactory implements IUserFactory {

    private final UserRepository userRepository;
    private final PatientFactory patientFactory;
    private final AdminFactory adminFactory;
    private final DoctorFactory doctorFactory;
    private final ReceptionistFactory receptionistFactory;

    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new UserAlreadyExitsException("Oops! " + registrationRequest.getEmail() + " already exists!");
        }
        switch (registrationRequest.getUserType()) {
            case "Doctor" -> {
                return doctorFactory.createDoctor(registrationRequest);
            }
            case "Patient" -> {
                return patientFactory.createPatient(registrationRequest);
            }
            case "Admin" -> {
                return adminFactory.createAdmin(registrationRequest);
            }
            case "Receptionist" -> {
                return receptionistFactory.createReceptionist(registrationRequest);
            }
            default -> {
                return null;
            }
        }
    }
}
