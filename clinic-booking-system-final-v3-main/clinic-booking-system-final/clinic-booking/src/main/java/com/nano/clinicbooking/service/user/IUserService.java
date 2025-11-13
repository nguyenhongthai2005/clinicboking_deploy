package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.dto.request.UserUpdateRequest;

import java.util.List;

public interface IUserService {

    User register(RegistrationRequest request);

    User update(Long userId, String role, UserUpdateRequest request);

    User findById(Long userId);

    void deleteById(Long userId);

    List<UserDto> getAllUsers();

    List<UserDto> getAllDisabledUsers();

}