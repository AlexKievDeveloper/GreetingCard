package com.greetingcard.service.impl;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CardUserService;
import com.greetingcard.service.CongratulationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DefaultCardUserService implements CardUserService {
    @Autowired
    private CardUserDao cardUserDao;
    @Autowired
    private SecurityService userService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CongratulationService congratulationService;

    @Override
    public void addUser(long cardId, User userAdmin, User userNewLogin) {
        String login = userNewLogin.getLogin();
        checkLoginForNotEmptyValue(login);
        long userId = checkLoginExistInDB(login);
        checkIfUserAdminForCard(cardId, userAdmin.getId(), "add");
        checkIfUserNotAdded(cardId, userId);
        checkIfCardNotFinished(cardId);
        cardUserDao.addUserMember(cardId, userId);
    }

    @Override
    public List<UserInfo> getUsersByCardId(long cardId, User userLoggedIn) {
        checkIfUserAdminForCard(cardId, userLoggedIn.getId(), "get");
        return cardUserDao.getUserMembersByCardId(cardId);
    }

    @Override
    @Transactional
    public void deleteUsers(long cardId, List<UserInfo> listUserIds, User userLoggedIn) {
        checkIfUserAdminForCard(cardId, userLoggedIn.getId(), "delete");
        if (listUserIds.size() > 0) {
            try {
                for (UserInfo listUserId : listUserIds) {
                    congratulationService.deleteByCardId(cardId, listUserId.getId());
                }
                cardUserDao.deleteListUsers(cardId, listUserIds);
            } catch (Exception e) {
                log.error("Error during deletion users ", e);
                throw new RuntimeException("Error during deletion users " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void deleteUserFromCard(long cardId, long userId) {
        congratulationService.deleteByCardId(cardId, userId);
        cardUserDao.deleteUserFromCard(cardId, userId);
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

    void checkIfUserAdminForCard(long cardId, long idUserAdded, String action) {
        Optional<Role> role = cardUserDao.getUserRole(cardId, idUserAdded);
        if (role.isEmpty() || role.get() != Role.ADMIN) {
            throw new IllegalArgumentException("Only card owner can " + action + " users");
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
