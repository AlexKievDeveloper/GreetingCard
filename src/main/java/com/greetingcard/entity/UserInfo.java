package com.greetingcard.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfo {
    private long id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String pathToPhoto;
}
