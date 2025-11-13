package com.nano.clinicbooking.repository.doctor;

import com.nano.clinicbooking.model.Doctor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    long countBySpecialtyId(Long specialtyId);

    @EntityGraph(attributePaths = {"specialty"})
    List<Doctor> findBySpecialtyId(Long specialtyId);

    @EntityGraph(attributePaths = {"specialty"})
    Optional<Doctor> findWithSpecialtyById(Long id);

}

