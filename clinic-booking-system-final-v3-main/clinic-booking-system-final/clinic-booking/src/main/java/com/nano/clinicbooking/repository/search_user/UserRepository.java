package com.nano.clinicbooking.repository.search_user;

import com.nano.clinicbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // 🟢 Lễ tân tìm kiếm user
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String fullName, String email, String phoneNumber);


}
