package com.social.employ.controller;

import com.social.employ.domain.entity.JobOffer;
import com.social.employ.domain.entity.User;
import com.social.employ.domain.enums.JobStatus;
import com.social.employ.domain.record.JobOfferRequest;
import com.social.employ.domain.record.JobOfferResponse;
import com.social.employ.repository.UserRepository;
import com.social.employ.service.JobOfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class JobOfferController {

    private final JobOfferService jobOfferService;
    private final UserRepository userRepository;

    public JobOfferController(JobOfferService jobOfferService, UserRepository userRepository) {
        this.jobOfferService = jobOfferService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    //create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<JobOfferResponse> create(@RequestBody @Valid JobOfferRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User company;

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (request.companyId() == null) {
                throw new RuntimeException("El ADMIN debe especificar un companyId");
            }
            company = userRepository.findById(request.companyId())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
        }
        //COMPANY, busca por su propio username
        else {
            company = getAuthenticatedUser();
        }

        JobOffer offer = new JobOffer();
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setCategory(request.category());
        offer.setWorkMode(request.workMode());

        JobOffer savedOffer = jobOfferService.createOffer(offer, company);
        return ResponseEntity.status(201).body(new JobOfferResponse(savedOffer));
    }

    //listar todas
    @GetMapping
    public ResponseEntity<List<JobOfferResponse>> list(@RequestParam(required = false) JobStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
        }

        var offers = jobOfferService.getAllOffers(currentUser, status);
        var response = offers.stream().map(JobOfferResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    //atualizar oferta
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<JobOfferResponse> update(@PathVariable Long id, @RequestBody @Valid JobOfferRequest request) {
        JobOffer updatedOffer = jobOfferService.updateOffer(id, request, getAuthenticatedUser());
        return ResponseEntity.ok(new JobOfferResponse(updatedOffer));
    }

    //busqueda de oferta por id
    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponse> getById(@PathVariable Long id) {
        JobOffer offer = jobOfferService.getOfferById(id);
        return ResponseEntity.ok(new JobOfferResponse(offer));
    }

    // Cerrar oferta delete soft
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<Void> closeOffer(@PathVariable Long id) {
        jobOfferService.toggleOfferStatus(id, getAuthenticatedUser(), false);
        return ResponseEntity.noContent().build();
    }

    // Reabrir oferta
    @PatchMapping("/{id}/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<Void> openOffer(@PathVariable Long id) {
        jobOfferService.toggleOfferStatus(id, getAuthenticatedUser(), true);
        return ResponseEntity.noContent().build();
    }

}