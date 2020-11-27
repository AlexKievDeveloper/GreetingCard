package com.greetingcard.service;

import com.greetingcard.entity.User;

public interface CardUserService {
    void addUser(long cardId, User userAdmin, User userNewLogin);
}
