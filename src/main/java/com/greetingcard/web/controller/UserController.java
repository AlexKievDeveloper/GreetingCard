package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Setter
@RestController
@RequestMapping(value = "/api/v1/")
public class UserController {
    private SecurityService securityService;
    @Autowired
    private int maxInactiveInterval;

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @DeleteMapping("session")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        log.info("Successfully logout");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "session", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Map<String, String> userCredential, HttpSession session) throws JsonProcessingException {
        log.info("login request");
        String login = userCredential.get("login");
        String password = userCredential.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        if (user == null) {
            log.info("Credentials not valid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(objectMapper
                    .writeValueAsString(Map.of("message",
                            "Access denied. Please check your login and password")));
        }
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(maxInactiveInterval);
        log.info("Successfully authentication");
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper
                .writeValueAsString(Map.of("login", login, "userId", user.getId())));
    }

    @PostMapping(value = "user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody Map<String, String> userCredentials) {
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
