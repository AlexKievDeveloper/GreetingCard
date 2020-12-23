package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class AuthController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Map<String, String> userCredentials) throws JsonProcessingException {
        log.info("login request");
        String login = userCredentials.get("login");
        String password = userCredentials.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        if (user == null) {
            log.info("Credentials not valid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper
                    .writeValueAsString(Map.of("message",
                            "Access denied. Please check your login and password")));
        }
        log.info(jwtProvider.toString());
        String token = jwtProvider.generateToken(login);
        log.info("Successfully authentication");

        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + token)
                .body(objectMapper
                        .writeValueAsString(Map.of("login", login, "userId", user.getId())));
    }
}
