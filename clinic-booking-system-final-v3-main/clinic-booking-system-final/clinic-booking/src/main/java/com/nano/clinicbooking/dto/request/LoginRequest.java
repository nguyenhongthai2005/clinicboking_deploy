package com.nano.clinicbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * ✅ Dùng cho API đăng nhập (/api/v1/auth/login)
 * React/Postman sẽ gửi JSON như:
 * {
 *    "email": "example@gmail.com",
 *    "password": "123456"
 * }
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
