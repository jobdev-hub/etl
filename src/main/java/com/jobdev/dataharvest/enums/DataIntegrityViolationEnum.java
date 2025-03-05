package com.jobdev.dataharvest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataIntegrityViolationEnum {

    UNIQUE_CONSTRAINT_VIOLATED("unique constraint"),
    UNKNOWN("unknown");

    private final String detail;

    public static DataIntegrityViolationEnum fromErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            return UNKNOWN;
        }

        if (errorMessage.contains("ORA-00001")) {
            return UNIQUE_CONSTRAINT_VIOLATED;
        }

        if (errorMessage.contains("unique constraint")) {
            return UNIQUE_CONSTRAINT_VIOLATED;
        }

        return UNKNOWN;
    }

}
