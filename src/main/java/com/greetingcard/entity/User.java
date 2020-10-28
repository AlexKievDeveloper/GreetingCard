package com.greetingcard.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String password;
    private String salt;
    private Language language;
}
