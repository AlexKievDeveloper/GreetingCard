package com.greetingcard.service;

import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.entity.UserOrder;

import java.util.List;

public interface CardUserService {
    void addUser(long cardId, User userAdmin, User userNewLogin);

    void addUser(long cardId, User userLoggedIn);

    List<UserInfo> getUsersByCardId(long cardId, User userLoggedIn);

    List<UserInfo> getUsersByCardIdForWebSocketNotification(long cardId);

    void deleteUsers(long cardId, List<UserInfo> listUserIds, User userLoggedIn);

    void deleteUserFromCard(long cardId, long userId);

    boolean verifyHash(long cardId, String hash);

    String getCardLink(long cardId);

    void changeUsersOrder(long cardId, long userId, List<UserOrder> usersOrder);
}
