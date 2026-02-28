package com.social.employ.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum WorkMode {
    REMOTE("Remote"),
    HYBRID("Hybrid"),
    ONSITE("On site");

    private final String label;

    WorkMode(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static WorkMode fromString(String value) {
        return Arrays.stream(WorkMode.values())
                .filter(a -> a.name().equalsIgnoreCase(value)
                        || a.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Mode invalid: " + value));
    }
}
