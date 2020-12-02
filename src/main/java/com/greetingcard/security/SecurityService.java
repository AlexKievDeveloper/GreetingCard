package com.greetingcard.security;

import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface SecurityService {

    User login(String login, String password);

    void save(User user);

    void update(User user, MultipartFile file);

    void updatePassword(User user);

    User findById(long id);

    User findByLogin(String login);

    User findByEmail(String email);

    Boolean verifyAccessHash(String hash, AccessHashType hashType);

    String generateAccessHash(String email, AccessHashType hashType);
}
