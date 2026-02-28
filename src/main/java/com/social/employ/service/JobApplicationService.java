package com.social.employ.service;

import com.social.employ.domain.entity.JobApplication;
import com.social.employ.domain.entity.JobOffer;
import com.social.employ.domain.entity.User;
import com.social.employ.domain.enums.ApplicationStatus;
import com.social.employ.domain.enums.JobStatus;
import com.social.employ.domain.enums.Role;
import com.social.employ.repository.JobApplicationRepository;
import com.social.employ.repository.JobOfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobOfferRepository offerRepository;

    public JobApplicationService(JobApplicationRepository applicationRepository, JobOfferRepository offerRepository) {
        this.applicationRepository = applicationRepository;
        this.offerRepository = offerRepository;
    }

    @Transactional
    public JobApplication applyToOffer(Long offerId, User candidate) {
        //busca offe
        JobOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("La oferta no existe"));

        //valida que este active
        if (offer.getStatus() != JobStatus.ACTIVE) {
            throw new RuntimeException("No puedes postularte a una oferta cerrada");
        }

        //valida si ya se postulo antes o no
        if (applicationRepository.existsByCandidateIdAndJobOfferId(candidate.getId(), offerId)) {
            throw new RuntimeException("Ya te has postulado a esta oferta");
        }

        //create postulación
        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJobOffer(offer);

        return applicationRepository.save(application);
    }

    //get de aplicaciones por candidatos id
    public List<JobApplication> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    //get de aplicaciones de candid por cada oferta
    public List<JobApplication> getApplicationsByOffer(Long offerId) {
        return applicationRepository.findByJobOfferId(offerId);
    }

    //metodo para cambiar los estados
    @Transactional
    public JobApplication updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, User user) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        //valida permisos: Si ADMIN pasa directo, si COMPANY es el due;o de la oferta
        boolean isAdmin = user.getRole() == Role.ADMIN;

        //get d la empresa que creó la oferta
        User companyOwner = application.getJobOffer().getCompany();

        if (!isAdmin && !companyOwner.getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para gestionar esta postulación");
        }

        application.setStatus(newStatus);
        if (newStatus == ApplicationStatus.HIRED) {
            JobOffer offer = application.getJobOffer();
            offer.setStatus(JobStatus.CLOSED);
        }
        return applicationRepository.save(application);
    }

    //listar ofertas que ve cada compa;ia
    public List<JobApplication> getApplicationsByCompany(Long companyId) {
        return applicationRepository.findByJobOffer_Company_Id(companyId);
    }
}
