package com.social.employ.domain.record;

import jakarta.validation.constraints.NotNull;

public record JobApplicationRequest(
        @NotNull(message = "El ID de la oferta es obligatorio")
        Long jobOfferId
) {}