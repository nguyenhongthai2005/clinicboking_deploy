package com.nano.clinicbooking.repository.grouphealth;


import com.nano.clinicbooking.model.groupheath.GroupHealthAssignment;
import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;


public interface GroupHealthAssignmentRepository extends JpaRepository<GroupHealthAssignment, Long> {
    List<GroupHealthAssignment> findByRequestId(Long requestId);
    boolean existsByRequestId(Long requestId);

    // ✅ Tải luôn request, specialty, shift, doctor để tránh lazy nhiều chỗ
    @EntityGraph(attributePaths = {"request", "specialty", "shift", "doctor"})
    List<GroupHealthAssignment> findByDoctorIdAndShiftDate(Long doctorId, LocalDate date);

}