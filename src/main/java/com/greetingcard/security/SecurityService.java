package com.greetingcard.security;

import com.greetingcard.entity.User;

public interface SecurityService {

    User login(String login, String password);

    void save(User user);

    void update(User user);

    User findById(long id);
}
