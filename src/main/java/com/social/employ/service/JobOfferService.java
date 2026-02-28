package com.social.employ.service;

import com.social.employ.domain.entity.JobOffer;
import com.social.employ.domain.entity.User;
import com.social.employ.domain.enums.JobStatus;
import com.social.employ.domain.enums.Role;
import com.social.employ.domain.record.JobOfferRequest;
import com.social.employ.repository.JobOfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobOfferService {
    private final JobOfferRepository jobOfferRepository;

    public JobOfferService(JobOfferRepository jobOfferRepository) {
        this.jobOfferRepository = jobOfferRepository;
    }

    @Transactional
    public JobOffer createOffer(JobOffer offer, User company) {
        if (company == null) {
            throw new RuntimeException("No se puede crear una oferta sin una empresa autenticada");
        }
        offer.setCompany(company);
        offer.setStatus(JobStatus.ACTIVE);
        return jobOfferRepository.save(offer);
    }

    //listar
    public List<JobOffer> getAllOffers(User currentUser, JobStatus filterStatus) {
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            if (filterStatus != null) return jobOfferRepository.findByStatus(filterStatus);
            return jobOfferRepository.findAll();
        }
        if (currentUser != null && currentUser.getRole() == Role.COMPANY) {
            if (filterStatus != null) return jobOfferRepository.findByCompanyIdAndStatus(currentUser.getId(), filterStatus);
            return jobOfferRepository.findByCompanyId(currentUser.getId());
        }
        if (filterStatus != null) {
            return jobOfferRepository.findByStatus(filterStatus);
        }
        return jobOfferRepository.findByStatus(JobStatus.ACTIVE);
    }

    //updtate
    @Transactional
    public JobOffer updateOffer(Long id, JobOfferRequest data, User user) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        //ADMIN o empresa dueña de la oferta puede updatear
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = offer.getCompany().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("No tienes permiso para editar esta oferta");
        }

        //actualiza los campos
        offer.setTitle(data.title());
        offer.setDescription(data.description());
        offer.setCategory(data.category());
        offer.setWorkMode(data.workMode());

        return jobOfferRepository.save(offer);
    }

    //busqueda de oferta por id
    public JobOffer getOfferById(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));
    }

    //cerrar o reabrir ofertas
    @Transactional
    public void toggleOfferStatus(Long id, User user, boolean open) {
        JobOffer offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        //admin
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = offer.getCompany().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("No tienes permiso para cambiar el estado de esta oferta");
        }

        //open es true -> ACTIVE, si es false -> CLOSED
        offer.setStatus(open ? JobStatus.ACTIVE : JobStatus.CLOSED);

        jobOfferRepository.save(offer);
    }
}