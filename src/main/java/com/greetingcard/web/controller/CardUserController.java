package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.service.CardUserService;
import com.greetingcard.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/")
public class CardUserController {
    private final CardUserService cardUserService;
    private final WebSocketService webSocketService;

    @PostMapping("card/{id}/user")
    public void addUserMember(@PathVariable long id, @RequestBody User user) {
        log.info("Request for adding user member for card {}, user {}", id, user.getLogin());
        User userLoggedIn = WebUtils.getCurrentUser();
        cardUserService.addUser(id, userLoggedIn, user);
        webSocketService.notifyAboutAddingToCard(userLoggedIn.getLogin() + " added you to card with id: " + id, user.getLogin());
        log.info("User successfully added to card");
    }

    @GetMapping("card/{id}/users")
    public List<UserInfo> getUsersByCard(@PathVariable long id) {
        log.info("Request get users by card {}", id);
        User userLoggedIn = WebUtils.getCurrentUser();
        List<UserInfo> userList = cardUserService.getUsersByCardId(id, userLoggedIn);
        log.info("Returned list of {} users", userList.size());
        return userList;
    }

    @DeleteMapping("card/{id}/users")
    public void deleteListMembers(@PathVariable long id, @RequestBody List<UserInfo> listUsers) {
        log.info("Request to delete {} users from card {}", listUsers.size(), id);
        User userLoggedIn = WebUtils.getCurrentUser();
        cardUserService.deleteUsers(id, listUsers, userLoggedIn);
        webSocketService.notifyAllDeletedCardMembers(listUsers, id);
        log.info("Users are successfully deleted from card {}", id);
    }

    @DeleteMapping("card/{id}/user")
    public void leaveCard(@PathVariable long id) {
        log.info("Request for leave card with id : {}", id);
        User user = WebUtils.getCurrentUser();
        log.info("Request for leave card from user with id : {}", user.getId());

        cardUserService.deleteUserFromCard(id, user.getId());
        webSocketService.notifyAdmin(user.getLogin() + " left your card with id: " + id, id);
        log.info("Successfully leave card with id: {}", id);
    }

}
