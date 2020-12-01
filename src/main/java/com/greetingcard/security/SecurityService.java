package com.greetingcard.security;

import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;

public interface SecurityService {

    User login(String login, String password);

    void save(User user);

    void update(User user);

    void updatePassword(User user);

    User findById(long id);

    User findByEmail(String email);

    void verifyAccessHash(String hash, AccessHashType hashType);

    String generateAccessHash(String email, AccessHashType hashType);
}
