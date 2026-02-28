package com.social.employ.repository;

import com.social.employ.domain.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    //todas sus postulaciones candidato
    List<JobApplication> findByCandidateId(Long candidateId);

    //Empresa ve quién se postuló a una oferta específica
    List<JobApplication> findByJobOfferId(Long jobOfferId);

    //evitar candidato se postule dos veces a misma oferta
    boolean existsByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);

    //ofertas por company
    List<JobApplication> findByJobOffer_Company_Id(Long companyId);
}
