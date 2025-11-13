package com.nano.clinicbooking.service.user;

import com.nano.clinicbooking.dto.EntityConverter;
import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.exception.ResourceNotFoundException;
import com.nano.clinicbooking.factory.UserFactory;
import com.nano.clinicbooking.model.User;
import com.nano.clinicbooking.repository.search_user.UserRepository;
import com.nano.clinicbooking.dto.request.RegistrationRequest;
import com.nano.clinicbooking.dto.request.UserUpdateRequest;
import com.nano.clinicbooking.service.email.send_register.AiEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final AiEmailService aiEmailService;// moi them
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final EntityConverter<User, UserDto> entityConverter;

    @Override
    public User register(RegistrationRequest request) {
            User user = userFactory.createUser(request);
            aiEmailService.sendRegisterSuccessEmail(user);
            return user;
        } // moi thay doi
    @Override
    public User update(Long userId, String role, UserUpdateRequest request) {
        if (!"Patient".equals(role)) {
            throw new SecurityException("Access denied: Only Patient can update personal information.");
        }

        User user = findById(userId);

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) user.setGender(request.getGender());

        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Soft delete (disable)
    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsEnable(false);
        userRepository.save(user);
    }

    // Kích hoạt lại user
    public void activateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsEnable(true);
        userRepository.save(user);
    }

    // Lấy tất cả user active
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(User::getIsEnable) // chỉ lấy user đang active
                .map(u -> entityConverter.mapEntityToDto(u, UserDto.class))
                .collect(Collectors.toList());
    }

    // Lấy tất cả user (Admin có thể xem cả đã khoá)
    public List<UserDto> getAllUsersIncludingDisabled() {
        return userRepository.findAll().stream()
                .map(u -> entityConverter.mapEntityToDto(u, UserDto.class))
                .collect(Collectors.toList());
    }

    // Lấy tất cả user bị khóa (Admin có thể xem)
    public List<UserDto> getAllDisabledUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.getIsEnable()) // chỉ lấy user bị khóa
                .map(u -> entityConverter.mapEntityToDto(u, UserDto.class))
                .collect(Collectors.toList());
    }


}
