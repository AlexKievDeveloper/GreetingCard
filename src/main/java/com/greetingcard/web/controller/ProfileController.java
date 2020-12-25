package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/user")
public class ProfileController {
    private final SecurityService service;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public User getUser() {
        User user = WebUtils.getCurrentUser();
        return User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .login(user.getLogin())
                .pathToPhoto(user.getPathToPhoto()).build();
    }

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public void updateUser(@RequestParam MultipartFile profileFile,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String login) {
        User user = WebUtils.getCurrentUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);

        service.update(user, profileFile);
    }

}
