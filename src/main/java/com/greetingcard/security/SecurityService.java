package com.greetingcard.security;

import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SecurityService {

    User login(String login, String password);

    void register(User user);

    void update(User user, MultipartFile file);

    void updatePassword(User user);

    User findByLogin(String login);

    void restorePassword(String email);

    void verifyEmailAccessHash(String hash);

    void verifyForgotPasswordAccessHash(String hash, String password);

    String generateAccessHash(String email, AccessHashType hashType);
    
    User loginWithFacebook(Map<String, String> facebookCredentials);

    User loginWithGoogle(Map<String, String> googleCredentials);
}
