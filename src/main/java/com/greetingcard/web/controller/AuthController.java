package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/")
public class AuthController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping(value = "auth", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Map<String, String> userCredentials) {
        log.info("login request");
        String login = userCredentials.get("login");
        String password = userCredentials.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        String token = jwtProvider.generateToken(login);
        log.info("Successfully authentication");

        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("login", login, "userId", user.getId(), "userLanguage", user.getLanguage().getName()));
    }

    @GetMapping(value = "invite_link/{id}/code/{hash}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> quickRegistrationByInvitingLink(@PathVariable long id, @PathVariable String hash) {
        log.info("request for quick login");
        boolean isVerified = securityService.verifyQuickLoginHash(id, hash);

        if (isVerified) {
            log.info("Successfully verified hash");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("cardId", id));
        } else {
            log.info("Not valid verification hash");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
