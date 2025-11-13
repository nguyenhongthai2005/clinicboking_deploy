package com.nano.clinicbooking.factory;

import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.dto.request.RegistrationRequest;

public interface IUserFactory {
    public User createUser(RegistrationRequest registrationRequest);
}
