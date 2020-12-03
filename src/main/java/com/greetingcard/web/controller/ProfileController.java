package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
@AllArgsConstructor
public class ProfileController {
    private SecurityService service;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        User profileUser = User.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .login(user.getLogin())
                .pathToPhoto(user.getPathToPhoto()).build();
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(profileUser);
    }

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestParam MultipartFile profileFile,
                                             @RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestParam String login,
                                             @RequestParam String pathToPhoto,
                                             HttpSession session){
        User user = (User) session.getAttribute("user");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        user.setPathToPhoto(pathToPhoto);

        service.update(user,profileFile);
        return ResponseEntity.status(HttpServletResponse.SC_OK).build();
    }

}
