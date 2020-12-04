package com.greetingcard.entity;

import lombok.Getter;

@Getter
public enum AccessHashType {
    VERIFY_EMAIL("emailVerification"), FORGOT_PASSWORD("forgotPassword");

    private final String type;

    AccessHashType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccessHashType{" +
                "type='" + type + '\'' +
                '}';
    }
}
