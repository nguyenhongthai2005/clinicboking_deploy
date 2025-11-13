package com.nano.clinicbooking.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResponse {
    private Long id;          // ID user
    private String fullName;
    private String email;
    private String phone;
}
