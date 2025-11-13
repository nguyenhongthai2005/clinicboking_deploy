package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.request.groupheath.GroupApproveRequest;
import com.nano.clinicbooking.dto.response.groupheath.GroupApprovalSummaryResponse;
import com.nano.clinicbooking.dto.response.groupheath.GroupHealthSummaryResponse;

import java.util.List;

public interface GroupHealthApprovalService {
    GroupApprovalSummaryResponse approve(Long requestId, GroupApproveRequest requestBody);

    GroupApprovalSummaryResponse reject(Long requestId, String reason);
    GroupApprovalSummaryResponse approveResultsAndSendToCustomer(Long requestId) throws Exception;

    List<GroupHealthSummaryResponse> getAllRequests(String status, int page, int size);
}
