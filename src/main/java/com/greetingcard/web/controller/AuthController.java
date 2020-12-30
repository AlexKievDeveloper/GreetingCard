package com.greetingcard.web.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> userCredentials) {
        log.info("login request");
        String login = userCredentials.get("login");
        String password = userCredentials.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);

        return getResponseEntityWithToken(user);
    }

    @PostMapping(value = "/facebook")
    public ResponseEntity<?> loginWithFacebook(@RequestBody Map<String, String> facebookCredentials) {
        log.info("login request");
        User user = securityService.loginWithFacebook(facebookCredentials);

        return getResponseEntityWithToken(user);
    }

    @PostMapping(value = "/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> googleCredentials) {
        User user = securityService.loginWithGoogle(googleCredentials);

        return getResponseEntityWithToken(user);
    }

    private ResponseEntity<?> getResponseEntityWithToken(User user) {
        String token = jwtProvider.generateToken(user.getLogin());
        log.info("Successfully authentication");
        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("login", user.getLogin(), "userId", user.getId(), "userLanguage", user.getLanguage().getName()));
    }

}
