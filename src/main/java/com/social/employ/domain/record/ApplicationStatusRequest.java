package com.social.employ.domain.record;

import com.social.employ.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        ApplicationStatus status
) {}