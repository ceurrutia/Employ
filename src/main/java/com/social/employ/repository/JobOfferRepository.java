package com.social.employ.repository;

import com.social.employ.domain.entity.JobOffer;
import com.social.employ.domain.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    //empresa ve solo lo suyo
    List<JobOffer> findByCompanyId(Long companyId);

    List<JobOffer> findByCompanyIdAndStatus(Long companyId, JobStatus status);

    //candidatos ven solo lo que está activo
    List<JobOffer> findByStatus(JobStatus status);
}
