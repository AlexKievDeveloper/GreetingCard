package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping(value = "/api/v1/")
public class UserController {
    private SecurityService securityService;
    @Value("${max.inactive.interval:3600}")
    private Integer maxInactiveInterval;

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> userCredentials, HttpSession session) {
        log.info("login request");
        String login = userCredentials.get("login");
        String password = userCredentials.get("password");
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(maxInactiveInterval);
        log.info("Successfully authentication");
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("login", login, "userId", user.getId()));
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
    public void changePassword(@RequestBody Map<String, String> userCredentials, HttpSession session) {
        User user = (User) session.getAttribute("user");
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