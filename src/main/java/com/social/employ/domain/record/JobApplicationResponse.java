package com.social.employ.domain.record;

import com.social.employ.domain.entity.JobApplication;
import com.social.employ.domain.enums.ApplicationStatus;
import java.time.LocalDateTime;

public record JobApplicationResponse(
        Long id,
        Long candidateId,
        String candidateName,
        Long jobOfferId,
        String jobOfferTitle,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public JobApplicationResponse(JobApplication application) {
        this(
                application.getId(),
                application.getCandidate().getId(),
                application.getCandidate().getUsername(),
                application.getJobOffer().getId(),
                application.getJobOffer().getTitle(),
                application.getStatus(),
                application.getAppliedAt()
        );
    }
}
