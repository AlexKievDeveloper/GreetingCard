package com.greetingcard.service;

import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;

import java.util.List;

public interface CardUserService {
    void addUser(long cardId, User userAdmin, User userNewLogin);

    List<UserInfo> getUsersByCardId(long cardId, User userLoggedIn);

    void deleteUsers(long cardId, List<UserInfo> listUserIds, User userLoggedIn);
}
