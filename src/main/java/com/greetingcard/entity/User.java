package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private Role role;
    private String password;
    private String salt;
    private Language language;
}
