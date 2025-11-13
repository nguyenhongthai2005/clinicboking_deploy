package com.nano.clinicbooking.mocks;

import com.nano.clinicbooking.model.User;
import java.util.Optional;

public class UserRepositoryMock {
    public static Optional<User> mockDoctor(Long id) {
        User user = new User();
        user.setId(id);
        user.setUserType("DOCTOR");
        user.setFullName("Dr. Test User");
        return Optional.of(user);
    }

    public static Optional<User> emptyUser() {
        return Optional.empty();
    }
}

