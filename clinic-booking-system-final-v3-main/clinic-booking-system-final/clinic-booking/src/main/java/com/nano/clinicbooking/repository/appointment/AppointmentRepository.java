package com.nano.clinicbooking.repository.appointment;

import com.nano.clinicbooking.enums.AppointmentStatus;
import com.nano.clinicbooking.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    int countByShiftIdAndStatusIn(Long shiftId, Collection<AppointmentStatus> statuses);

    @EntityGraph(attributePaths = {"patient","doctor","specialty","shift"})
    Optional<Appointment> findWithAllById(Long id);

    @EntityGraph(attributePaths = {"patient","doctor","specialty","shift"})
    List<Appointment> findAllByStatus(AppointmentStatus status);

    @EntityGraph(attributePaths = {"patient","doctor","specialty","shift"})
    List<Appointment> findAll();

    List<Appointment> findByShiftIdAndStatus(Long shiftId, AppointmentStatus status);

    List<Appointment> findByPatientIdAndStatusIn(Long patientId, List<AppointmentStatus> statuses);

    List<Appointment> findByShiftIdAndStatusIn(Long shiftId, List<AppointmentStatus> statuses);

    int countByShiftIdAndStatusIn(Long shiftId, List<AppointmentStatus> statuses);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    List<Appointment> findByStatusAndAppointmentDate(AppointmentStatus status, LocalDate date);

    List<Appointment> findAllByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);


    long countByShiftId(Long shiftId);

    //dem so lan huy cancel
    long countByPatientIdAndStatus(Long patientId, AppointmentStatus status);
}

