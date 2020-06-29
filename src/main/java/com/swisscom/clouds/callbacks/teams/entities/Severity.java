package com.swisscom.clouds.callbacks.teams.entities;

public enum Severity {

    SUCCESS("31E243"),
    INFO("4286f4"),
    WARN("E75128"),
    ERROR("BC0D0D");

    private final String code;

    Severity(String code) {
        this.code = code;
    }

    public static Severity fromCode(String code) {
        for (Severity b : Severity.values()) {
            if (b.code.equals(code)) {
                return b;
            }
        }
        return WARN;
    }

    public String getCode() {
        return code;
    }
}