package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Setter
@RestController
@RequestMapping(value = "/api/v1/user/",produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    SecurityService service;

    public ProfileController(SecurityService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(HttpSession session){
        User user = (User) session.getAttribute("user");
        user.setSalt(null);
        user.setPassword(null);
        user.setGoogle(null);
        user.setFacebook(null);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(user);
    }

}
