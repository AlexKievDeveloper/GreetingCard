package com.greetingcard.security;

import com.greetingcard.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Session {
    private String token;
    private User user;
    private LocalDateTime expireDate;
}
