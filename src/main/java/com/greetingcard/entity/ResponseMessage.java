package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ResponseMessage {
    ACCESS_DENIED("Access denied. Please check your login and password."),
    AUTHENTICATION_SUCCESS("Authentication success");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
