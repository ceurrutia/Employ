package com.social.employ.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role {
    USER("User"),
    ADMIN("Admin"),
    COMPANY("Company");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Role fromString(String value) {
        return Arrays.stream(Role.values())
                .filter(a -> a.name().equalsIgnoreCase(value)
                        || a.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rol inválido: " + value));
    }
}
