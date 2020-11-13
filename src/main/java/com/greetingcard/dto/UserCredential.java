package com.greetingcard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCredential {
    private String user;
    private String password;
}
