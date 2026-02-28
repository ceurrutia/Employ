package com.social.employ.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ApplicationStatus {
    RECEIVED("Received"),
    IN_PROGRESS("In progress"),
    INTERVIEW("Interview"),
    REJECTED("Rejected"),
    HIRED("Hired");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ApplicationStatus fromString(String value) {
        return Arrays.stream(ApplicationStatus.values())
                .filter(a -> a.name().equalsIgnoreCase(value)
                        || a.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }
}
