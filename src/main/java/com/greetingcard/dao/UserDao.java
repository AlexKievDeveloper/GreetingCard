package com.greetingcard.dao;

import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;

public interface UserDao {

    void save(User user);

    void update(User user);

    void updatePassword(User user);

    User findByLogin(String login);

    User findByEmail(String email);

    User findByForgotPasswordAccessHash(String hash);

    void saveAccessHash(String email, String hash, AccessHashType hashType);

    void verifyEmailAccessHash(String hash);

    void verifyForgotPasswordAccessHash(String hash, User user);

    void saveUserFromFacebook(User user);

    void saveUserFromGoogle(User user);
}