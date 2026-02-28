package com.social.employ.domain.record;

import com.social.employ.domain.enums.Role;

public record UserRegistrationRequest(
        String username,
        String email,
        String password,
        Role role,
        String bio,
        String cvUrl,
        String companyName
) {}
