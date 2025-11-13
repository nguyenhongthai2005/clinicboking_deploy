package com.nano.clinicbooking.repository.doctor;

import com.nano.clinicbooking.enums.ShiftStatus;
import com.nano.clinicbooking.model.DoctorShift;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DoctorShiftRepository extends JpaRepository<DoctorShift, Long> {

    List<DoctorShift> findByDoctorId(Long doctorId);

    List<DoctorShift> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    List<DoctorShift> findByDate(LocalDate date);

    List<DoctorShift> findByDoctorIdAndDateAndStatus(Long doctorId, LocalDate date, ShiftStatus status);

    @EntityGraph(attributePaths = {"doctor", "doctor.specialty"})
    List<DoctorShift> findByDateBetween(LocalDate start, LocalDate end);

//    thay doi mini

    @EntityGraph(attributePaths = {"doctor", "doctor.specialty"})
    @Query("""
        select ds from DoctorShift ds
        join ds.doctor d
        join d.specialty s
        where s.id = :specialtyId
          and ds.date between :from and :to
          and ds.status = com.nano.clinicbooking.enums.ShiftStatus.ACTIVE
        order by ds.date asc, ds.shift asc, d.fullName asc
    """)
    List<DoctorShift> findWeeklySchedule(
            @Param("specialtyId") Long specialtyId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}

