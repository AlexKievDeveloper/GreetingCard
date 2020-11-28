package com.greetingcard.service.impl;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CardUserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class DefaultCardUserService implements CardUserService {
    private final CardUserDao cardUserDao;
    private final SecurityService userService;
    private final CardService cardService;

    @Override
    public void addUser(long cardId, User userAdmin, User userNewLogin) {
        String login = userNewLogin.getLogin();
        checkLoginForNotEmptyValue(login);
        long userId = checkLoginExistInDB(login);
        checkIfUserAdminForCard(cardId, userAdmin.getId());
        checkIfUserNotAdded(cardId, userId);
        checkIfCardNotFinished(cardId);
        cardUserDao.addUserMember(cardId, userId);
    }

    void checkIfCardNotFinished(long cardId) {
        Optional<Status> status = cardService.getCardStatusById(cardId);
        if (status.isEmpty()) {
            throw new IllegalArgumentException("Card does not exist");
        }
        if (status.get() == Status.ISOVER) {
            throw new IllegalArgumentException("Card is already finished");
        }
    }

    void checkIfUserNotAdded(long cardId, long userId) {
        Optional<Role> role = cardUserDao.getUserRole(cardId, userId);
        if (role.isPresent()) {
            throw new IllegalArgumentException("User is already member");
        }
    }

    void checkIfUserAdminForCard(long cardId, long idUserAdded) {
        Optional<Role> role = cardUserDao.getUserRole(cardId, idUserAdded);
        if (role.isEmpty() || role.get() != Role.ADMIN) {
            throw new IllegalArgumentException("Only card owner can add users");
        }
    }

    long checkLoginExistInDB(String login) {
        User user = userService.findByLogin(login);
        if (user == null) {
            throw new IllegalArgumentException("Login does not exist");
        }
        return user.getId();
    }

    void checkLoginForNotEmptyValue(String login) {
        if (login.equals("")) {
            throw new IllegalArgumentException("Login of user is empty");
        }
    }
}
