package com.social.employ.domain.record;

import java.util.List;

public record GroupedApplicationsResponse(
        String jobOfferTitle,
        List<JobApplicationResponse> applicants
) {}
