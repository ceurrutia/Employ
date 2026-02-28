package com.social.employ.domain.record;

import com.social.employ.domain.enums.JobCategory;
import com.social.employ.domain.enums.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobOfferRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
                              String title,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 20, message = "La descripción debe ser más detallada (mínimo 20 caracteres)")
                              String description,

        @NotNull(message = "La categoría es obligatoria")
        JobCategory category,

        @NotNull(message = "La modalidad de trabajo es obligatoria")
        WorkMode workMode,

        Long companyId
) {
}