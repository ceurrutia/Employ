package com.social.employ.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum JobCategory {
    ADMINISTRATION("Administration"),
    IT("IT"),
    SYSTEMS("Systems"),
    QA("QA"),
    FUNCTIONAL_ANALYST("Functional Analyst"),
    DESIGN("Design"),
    MARKETING("Marketing");

    private final String label;

    JobCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static JobCategory fromString(String value) {
        return Arrays.stream(JobCategory.values())
                .filter(a -> a.name().equalsIgnoreCase(value)
                        || a.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + value));
    }
}
