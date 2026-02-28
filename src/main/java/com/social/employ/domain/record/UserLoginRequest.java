package com.social.employ.domain.record;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        String username,
        String email,
        @NotBlank String password
) {}
