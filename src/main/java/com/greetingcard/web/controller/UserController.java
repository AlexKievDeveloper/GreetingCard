package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/")
@PropertySource(value = "classpath:application.properties")
public class UserController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private EmailService emailService;
    @Value("${max.inactive.interval}")
    private int maxInactiveInterval;

    private ObjectMapper objectMapper = new ObjectMapper();

    @DeleteMapping("session")
    public ResponseEntity logout(HttpSession session) {
        session.invalidate();
        log.info("Successfully logout");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "session", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody Map<String, String> userCredential, HttpSession session) throws JsonProcessingException {
        log.info("login request");
        String login = userCredential.get("user");
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
    public ResponseEntity register(@RequestBody Map<String, String> userCredentials) {
        User user = User.builder()
                .firstName(userCredentials.get("firstName"))
                .lastName(userCredentials.get("lastName"))
                .email(userCredentials.get("email"))
                .login(userCredentials.get("login"))
                .password(userCredentials.get("newPassword"))
                .build();
        log.info("Registration request for user login: {}", user.getLogin());
        securityService.save(user);
        log.info("Successfully registered: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "user/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(@RequestParam String login, @RequestParam String oldPassword, @RequestParam String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("user");

        user = securityService.login(login, oldPassword);

        log.debug("Request to change password for user with login: {}", user.getLogin());
        if (Objects.isNull(user)) {
            log.debug("User check failed for login: {}. REASON: Login or password is incorrect", login);
            throw new RuntimeException("User check failed. Login or password is incorrect.");
        }
        user.setPassword(newPassword);
        securityService.updatePassword(user);
    }

    @PostMapping(value = "user/forgot_password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendLetterToRestorePassword(@RequestParam String email) {
        log.debug("Request to restore forgotten password for user with email: {}", email);
        User user = securityService.findByEmail(email);

        log.debug("Sending letter with forgotten password to user with email address: {}", user.getEmail());
        String emailMessageBody = "Your password is: " + user.getPassword();
        emailService.sendMail(user.getEmail(), "Greeting Card: Restore password", emailMessageBody);
    }

    @PostMapping(value = "user/forgot_password/{access-token}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void restoreAccessToProfile(@PathVariable String accessToken) {
        securityService.verifyAccessHash(accessToken, AccessHashType.FORGOT_PASSWORD);
    }

    @PostMapping(value = "user/email/verify/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendLetterToVerifyEmail(@RequestParam String email, @Value("${web.app.url}") String siteUrl) {
        User user = securityService.findByEmail(email);

        if (Objects.nonNull(user)) {
            String emailRandomHash = securityService.generateAccessHash(email, AccessHashType.VERIFY_EMAIL);
            log.debug("Sending letter to verify user's email address: {}", email);
            String emailMessageBody = "Please confirm your email address by opening this link: " +
                    siteUrl + "/email/verify/" + emailRandomHash;
            emailService.sendMail(email, "Greeting Card: Verify email", emailMessageBody);
        }
    }

    @PostMapping(value = "user/email/verify/{access-token}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void verifyEmail(@PathVariable String accessToken) {
        securityService.verifyAccessHash(accessToken, AccessHashType.VERIFY_EMAIL);
    }
}
