package com.nano.clinicbooking.service.receptionist;

import com.nano.clinicbooking.dto.response.UserDto;
import com.nano.clinicbooking.repository.receptionist.ReceptionistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionistService {

    private final ReceptionistRepository receptionistRepository;

    public List<UserDto> getAllReceptionists() {
        return receptionistRepository.findAll().stream()
                .filter(r -> r.getIsEnable()) // chỉ lấy active
                .map(r -> {
                    UserDto dto = new UserDto();
                    dto.setId(r.getId());
                    dto.setFullName(r.getFullName());
                    dto.setEmail(r.getEmail());
                    dto.setPhoneNumber(r.getPhoneNumber());
                    dto.setGender(r.getGender());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
