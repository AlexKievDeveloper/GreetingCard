package com.greetingcard.dao;

import com.greetingcard.entity.User;

public interface UserDao {

    User findUserByLogin(String login);
}
