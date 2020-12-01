package com.greetingcard.entity;

import lombok.Getter;

@Getter
public enum AccessHashType {
    VERIFY_EMAIL("verify_email_hashes"), FORGOT_PASSWORD("forgot_password_hashes");

    private final String tableName;

    AccessHashType(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "AccessHashType{" +
                "tableName='" + tableName + '\'' +
                '}';
    }
}
