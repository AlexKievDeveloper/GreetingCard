package com.greetingcard.service.impl;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.entity.*;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CardUserService;
import com.greetingcard.service.CongratulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCardUserService implements CardUserService {
    private final CardUserDao cardUserDao;
    private final SecurityService userService;
    private final CardService cardService;
    private final CongratulationService congratulationService;
    @Value("${webapp.url}")
    private String siteUrl;

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
    public void addUser(long cardId, User userLoggedIn) {
        checkIfUserNotAdded(cardId, userLoggedIn.getId());
        checkIfCardNotFinished(cardId);
        cardUserDao.addUserMember(cardId, userLoggedIn.getId());
    }

    @Override
    public List<UserInfo> getUsersByCardId(long cardId, User userLoggedIn) {
        checkIfUserAdminForCard(cardId, userLoggedIn.getId(), "get");
        return cardUserDao.getUserMembersByCardId(cardId);
    }

    @Override
    public List<UserInfo> getUsersByCardIdForWebSocketNotification(long cardId) {
        return cardUserDao.getUserMembersByCardIdForWebSocketNotification(cardId);
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

    @Override
    public String getCardLink(long cardId) {
        String hash = generateHashForInviteLink(cardId);
        saveHash(cardId, hash);
        return siteUrl + "invite_link/" + cardId + "/code/"+ hash;
    }

    @Override
    public void changeUsersOrder(long cardId, long userId, List<UserOrder> usersOrder) {
        checkIfUserAdminForCard(cardId, userId, "change order of");
        cardUserDao.changeUsersOrder(cardId, usersOrder);
    }

    @Override
    public boolean verifyHash(long cardId, String hash) {
        List<String> actualHashes = cardUserDao.getCardHashesByCardId(cardId);
        if (actualHashes.contains(hash)) {
            return true;
        }
        log.info("Hash is not valid for card with id: {}", cardId);
        return false;
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
            log.debug("Exception while checking user Role. Only card owner can " + action + " users");
            throw new IllegalArgumentException("Only card owner can " + action + " users");
        }
    }

    long checkLoginExistInDB(String login) {
        User user = userService.findByLogin(login);
        return user.getId();
    }

    void checkLoginForNotEmptyValue(String login) {
        if (login.equals("")) {
            throw new IllegalArgumentException("Login of user is empty");
        }
    }

    String generateHashForInviteLink(long cardId) {
        return UUID.randomUUID().toString().replaceAll("/", "");
    }

    void saveHash(long cardId, String hash) {
        cardUserDao.saveHash(cardId, hash);
    }
}
