package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping(value = "/api/v1/")
public class UserController {
    private final SecurityService securityService;
    @Value("${max.inactive.interval:3600}")
    private Integer maxInactiveInterval;

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
        securityService.register(user);

        log.info("Successfully registered: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("user/verification/{accessHash}")
    public void verifyEmail(@PathVariable String accessHash) {
        log.info("Verifying the user");
        securityService.verifyEmailAccessHash(accessHash);
    }

    @PutMapping(value = "user/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(@RequestBody Map<String, String> userCredentials) {
        User user = WebUtils.getCurrentUser();
        String login = user.getLogin();
        String oldPassword = userCredentials.get("oldPassword");
        user = securityService.login(login, oldPassword);

        if (user == null) {
            log.debug("Login or old password value is incorrect.");
            throw new IllegalArgumentException("Login or or old password value is incorrect.");
        }

        String newPassword = userCredentials.get("newPassword");
        user.setPassword(newPassword);

        log.debug("Request to change password for user with login: {}", user.getLogin());
        securityService.updatePassword(user);
    }

    @PostMapping(value = "user/forgot_password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void restorePassword(@RequestBody Map<String, String> userCredentials) {
        String email = userCredentials.get("email");
        log.debug("Request to restore forgotten password for user with email: {}", email);
        securityService.restorePassword(email);
    }

    @PutMapping("user/recover_password/{accessHash}")
    public void restoreAccessToProfile(@RequestBody Map<String, String> userCredentials, @PathVariable String accessHash) {
        String password = userCredentials.get("password");
        securityService.verifyForgotPasswordAccessHash(accessHash, password);
    }
}