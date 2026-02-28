package com.social.employ.controller;

import com.social.employ.domain.entity.JobApplication;
import com.social.employ.domain.entity.User;
import com.social.employ.domain.record.ApplicationStatusRequest;
import com.social.employ.domain.record.GroupedApplicationsResponse;
import com.social.employ.domain.record.JobApplicationRequest;
import com.social.employ.domain.record.JobApplicationResponse;
import com.social.employ.repository.UserRepository;
import com.social.employ.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserRepository userRepository;

    public JobApplicationController(JobApplicationService applicationService, UserRepository userRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    //create
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JobApplicationResponse> apply(@RequestBody @Valid JobApplicationRequest request) {
        User candidate = getAuthenticatedUser();
        var application = applicationService.applyToOffer(request.jobOfferId(), candidate);
        return ResponseEntity.status(201).body(new JobApplicationResponse(application));
    }

    //listar mis postulaciones (para el candidato)
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications() {
        User candidate = getAuthenticatedUser();
        var applications = applicationService.getApplicationsByCandidate(candidate.getId());
        var response = applications.stream().map(JobApplicationResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    //listar postulantes a una oferta (para la empresa/admin)
    @GetMapping("/offer/{offerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<List<JobApplicationResponse>> getByOffer(@PathVariable Long offerId) {
        var applications = applicationService.getApplicationsByOffer(offerId);
        var response = applications.stream().map(JobApplicationResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    //metodo para cambiar los estados de una postulacion
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid ApplicationStatusRequest request) {

        User currentUser = getAuthenticatedUser();
        JobApplication updated = applicationService.updateApplicationStatus(id, request.status(), currentUser);
        return ResponseEntity.ok(new JobApplicationResponse(updated));
    }

    //todas las postulaciones recibidas por la empresa autenticada
    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<GroupedApplicationsResponse>> getApplicationsForMyCompany() {
        User company = getAuthenticatedUser();
        List<JobApplication> applications = applicationService.getApplicationsByCompany(company.getId());
        // Agrupamiento por título de oferta
        List<GroupedApplicationsResponse> response = applications.stream()
                .collect(Collectors.groupingBy(app -> app.getJobOffer().getTitle()))
                .entrySet().stream()
                .map(entry -> new GroupedApplicationsResponse(
                        entry.getKey(),
                        entry.getValue().stream().map(JobApplicationResponse::new).toList()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
