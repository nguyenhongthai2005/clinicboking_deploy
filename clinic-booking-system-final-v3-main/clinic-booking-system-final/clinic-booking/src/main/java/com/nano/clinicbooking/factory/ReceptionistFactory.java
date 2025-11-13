package com.nano.clinicbooking.factory;

import com.nano.clinicbooking.model.Receptionist;
import com.nano.clinicbooking.repository.receptionist.ReceptionistRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceptionistFactory {
    private final ReceptionistRepository receptionistRepository;
    private final UserAttributesMapper userAttributesMapper;


    public Receptionist createReceptionist(RegistrationRequest request) {
        Receptionist receptionist = new Receptionist();
        userAttributesMapper.setCommonAttributes(request, receptionist);
        return receptionistRepository.save(receptionist);
    }
}
