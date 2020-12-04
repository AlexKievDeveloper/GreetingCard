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
    public List<UserInfo> getUsersByCard(@PathVariable long id, HttpSession session) {
        log.info("Request get users by card {}", id);
        User userLoggedIn = (User) session.getAttribute("user");
        List<UserInfo> userList = cardUserService.getUsersByCardId(id, userLoggedIn);
        log.info("Returned list of {} users", userList.size());
        return userList;
    }

    @DeleteMapping("card/{id}/users")
    public void deleteListMembers(@PathVariable long id, @RequestBody List<UserInfo> listUsers, HttpSession session) {
        log.info("Request to delete {} users from card {}", listUsers.size(), id);
        User userLoggedIn = (User) session.getAttribute("user");
        cardUserService.deleteUsers(id, listUsers, userLoggedIn);
        log.info("Users are successfully deleted from card {}", id);
    }

    @DeleteMapping("card/{id}/user")
    public void leaveCard(@PathVariable long id, HttpSession session) {
        log.info("Request for leave card with id : {}", id);
        User user = (User) session.getAttribute("user");

        log.info("Request for leave card from user with id : {}", user.getId());
        cardUserService.deleteUserFromCard(id, user.getId());

        log.info("Successfully leave card with id: {}", id);
    }

}
