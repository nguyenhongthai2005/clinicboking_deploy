package com.nano.clinicbooking.repository.grouphealth;

import com.nano.clinicbooking.model.groupheath.GroupHealthRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupHealthRequestRepository extends JpaRepository<GroupHealthRequest, Long> {
    Page<GroupHealthRequest> findByStatus(GroupHealthRequest.RequestStatus status, Pageable pageable);
}
