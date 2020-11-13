package com.greetingcard.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.dto.AuthenticationResponse;
import com.greetingcard.dto.UserCredential;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.greetingcard.entity.ResponseMessage.ACCESS_DENIED;
import static com.greetingcard.entity.ResponseMessage.AUTHENTICATION_SUCCESS;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private int maxInactiveInterval;


    @DeleteMapping
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        log.info("Successfully logout");
    }

    @PostMapping(value = "/api/v1/session", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthenticationResponse login(@RequestBody UserCredential userCredential, HttpSession session) {
        log.info("login request");
        String login = userCredential.getUser();
        String password = userCredential.getPassword();
        log.info("login for user {}", login);
        User user = securityService.login(login, password);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        if (user != null) {

            session.setAttribute("user", user);
            session.setMaxInactiveInterval(maxInactiveInterval);

            authenticationResponse.setLogin(user.getLogin());
            authenticationResponse.setMessage(AUTHENTICATION_SUCCESS.getMessage());
            log.info("Successfully authentication");

        } else {
            authenticationResponse.setMessage(ACCESS_DENIED.getMessage());
            log.info("Credentials not valid");
        }
        return authenticationResponse;
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody String json) {
        Map<String, String> userMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
                });

        User user = User.builder()
                .firstName(userMap.get("firstName"))
                .lastName(userMap.get("lastName"))
                .email(userMap.get("email"))
                .login(userMap.get("login"))
                .password(userMap.get("password"))
                .build();

        log.info("Registration request for user login: {}", user.getLogin());
        securityService.save(user);
        log.info("Successfully registered: {}", user);
    }
}
