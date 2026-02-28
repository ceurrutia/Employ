package com.social.employ.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum JobStatus {
    ACTIVE("Active"),
    CLOSED("Closed");

    private final String label;

    JobStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static JobStatus fromString(String value) {
        return Arrays.stream(JobStatus.values())
                .filter(a -> a.name().equalsIgnoreCase(value)
                        || a.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }


}
