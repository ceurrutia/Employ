package com.social.employ.domain.record;

import com.social.employ.domain.enums.JobCategory;
import com.social.employ.domain.enums.JobStatus;
import com.social.employ.domain.enums.WorkMode;
import com.social.employ.domain.entity.JobOffer;

import java.time.LocalDateTime;

public record JobOfferResponse(
        Long id,
        String title,
        String description,
        String companyName,
        JobCategory category,
        WorkMode workMode,
        JobStatus status
) {
    // Constructor Entidad -> Record
    public JobOfferResponse(JobOffer offer) {
        this(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getCompany().getCompanyName(),
                offer.getCategory(),
                offer.getWorkMode(),
                offer.getStatus()
        );
    }
}