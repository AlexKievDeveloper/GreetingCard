package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
public class ProfileController {
    private SecurityService service;
    //TODO:set value from properties
    private String uploadPathFilm = "src/main/webapp/static/userPhoto";

    public ProfileController(SecurityService service) {
        this.service = service;
    }

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

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateUser(@RequestParam MultipartFile file,
                                             @RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestParam String login,
                                             @RequestParam String pathToPhoto,
                                             HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);

        if (file.isEmpty()) {
            String uuidFile = UUID.randomUUID().toString();
            String fileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPathFilm + "/" + fileName));
            Files.deleteIfExists(Path.of(pathToPhoto));
            user.setPathToPhoto(fileName);
        }
        service.update(user);
        return ResponseEntity.status(HttpServletResponse.SC_OK).build();
    }

}
