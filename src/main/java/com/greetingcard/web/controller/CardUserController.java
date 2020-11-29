package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.service.CardUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

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
        log.info("User successfully added to card");
    }

    @GetMapping("card/{id}/users")
    public List<UserInfo> getUsersByCard(@PathVariable long id) {
        log.info("Request get users by card {}", id);
        List<UserInfo> userList = cardUserService.getUsersByCardId(id);
        log.info("Returned list of {} users", userList.size());
        return userList;
    }

}
