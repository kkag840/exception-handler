package com.common.exception;

public enum DefaultErrorCodes implements ErrorCodes {

    NOT_NULL_VALIDATION("COMMON_EXCEPTION_0000"),
    DATABASE_EXCEPTION("COMMON_EXCEPTION_0001"),
    VALIDATION_FAILED("COMMON_EXCEPTION_0002"),;

    private final String value;

    DefaultErrorCodes(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDefaultMessage() {
        return null;
    }

}
