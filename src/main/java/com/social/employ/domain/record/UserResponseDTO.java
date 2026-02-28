package com.social.employ.domain.record;

import com.social.employ.domain.entity.User;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String role,
        String bio,
        String profilePictureUrl,
        String cvUrl,
        String companyName,
        LocalDateTime createdAt,
        boolean active
) {
    // Constructor convierte
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getBio(),
                user.getProfilePictureUrl(),
                user.getCvUrl(),
                user.getCompanyName(),
                user.getCreatedAt(),
                user.isActive()
        );
    }
}
