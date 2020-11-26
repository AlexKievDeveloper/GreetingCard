package com.greetingcard.dao;

import com.greetingcard.entity.User;

public interface UserDao {

    User findByLogin(String login);

    void save (User user);

    void update(User user);
}
