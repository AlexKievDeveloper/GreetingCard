package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.service.CardUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("api/v1/")
public class CardUserController {
    @Autowired
    private CardUserService cardUserService;

    @PostMapping("card/{id}/user")
    public void addUserMember(@PathVariable long id, @RequestBody User user, HttpSession session) {
        log.info("Request for adding user member for card {}, user {}", id, user.getLogin());
        User userLoggedIn = (User) session.getAttribute("user");
        cardUserService.addUser(id, userLoggedIn, user);
    }
}
