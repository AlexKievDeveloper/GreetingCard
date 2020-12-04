package com.greetingcard.dao;

import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao {

    void save (User user);

    void update(User user);

    void updatePassword(User user);

    User findByLogin(String login);

    User findById(long id);

    User findByEmail(String email);

    void saveAccessHash(String email, String hash, AccessHashType hashType);

    @Transactional
    Boolean verifyEmailAccessHash(String hash);

    @Transactional
    Boolean verifyForgotPasswordAccessHash(String hash, String password);
}
