package com.nano.clinicbooking.repository;

import com.nano.clinicbooking.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
    Optional<Specialty> findByName(String name);

    List<Specialty> findByNameIn(List<String> names);

}
