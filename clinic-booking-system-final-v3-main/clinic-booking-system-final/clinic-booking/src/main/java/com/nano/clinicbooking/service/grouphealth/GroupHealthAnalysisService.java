package com.nano.clinicbooking.service.grouphealth;

import com.nano.clinicbooking.dto.response.groupheath.GroupHealthAnalysisResponse;
import java.io.IOException;

public interface GroupHealthAnalysisService {
    GroupHealthAnalysisResponse analyze(Long requestId) throws IOException;
}
