package com.greetingcard.entity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Role {
    ADMIN("ADMIN", 1), MEMBER("MEMBER", 2);

    private final String role;
    private final int number;

    Role(String userRole, int number) {
        this.role = userRole;
        this.number = number;
    }

    public static Role getByNumber(int number) {
        Role[] userRoles = Role.values();
        for (Role userRole : userRoles) {
            if (userRole.getRoleNumber() == number) {
                return userRole;
            }
        }
        log.error("No role for number: {}", number);
        throw new IllegalArgumentException("No role for number " + number);
    }

    public int getRoleNumber() {
        return number;
    }

    public String getUserRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role='" + role + '\'' +
                ", number=" + number +
                '}';
    }
}

