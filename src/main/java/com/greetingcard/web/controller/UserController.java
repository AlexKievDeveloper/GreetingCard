package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.Map;


@Slf4j
@RestController
public class UserController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private int maxInactiveInterval;


    @DeleteMapping(value = "/api/v1/session")
    public ResponseEntity logout(HttpSession session) {
        session.invalidate();
        log.info("Successfully logout");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/api/v1/session", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody Map<String, String> userCredential, HttpSession session) {
        log.info("login request");
        String login = userCredential.get("user");
        String password = userCredential.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        if (user == null) {
            log.info("Credentials not valid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Access denied. Please check your login and password"));
        }
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(maxInactiveInterval);
        log.info("Successfully authentication");
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }

    @PostMapping(value = "/api/v1/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody Map<String, String> userCredentials) {
        User user = User.builder()
                .firstName(userCredentials.get("firstName"))
                .lastName(userCredentials.get("lastName"))
                .email(userCredentials.get("email"))
                .login(userCredentials.get("login"))
                .password(userCredentials.get("password"))
                .build();
        log.info("Registration request for user login: {}", user.getLogin());
        securityService.save(user);
        log.info("Successfully registered: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
