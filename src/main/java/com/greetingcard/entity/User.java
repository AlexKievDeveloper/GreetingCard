package com.greetingcard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String password;
    private String salt;
    private Language language;
    private String google;
    private String facebook;
    private String pathToPhoto;
}
