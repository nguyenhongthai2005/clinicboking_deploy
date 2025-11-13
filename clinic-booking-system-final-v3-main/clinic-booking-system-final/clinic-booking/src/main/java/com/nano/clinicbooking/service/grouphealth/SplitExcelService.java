package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.response.groupheath.CapacitySplitResponse;

import java.util.List;

public interface SplitExcelService {
    CapacitySplitResponse splitAndSuggest(Long requestId, int maxPerFile, List<String> shiftOrder) throws Exception;
}
