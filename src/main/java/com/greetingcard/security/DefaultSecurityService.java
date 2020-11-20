package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Setter
public class DefaultSecurityService implements SecurityService {

    private UserDao userDao;

    private String algorithm;

    private int iteration;

    @Override
    public User login(String login, String password) {
        log.info("login: {}", login);
        checkUserLogin(login);
        User user = userDao.findByLogin(login);

        if (user != null) {
            String salt = user.getSalt();
            String hashPassword = getHashPassword(salt.concat(password));

            if (user.getPassword().equals(hashPassword)) {
                log.info("RETURN USER!");
                return user;
            }
        }
        return null;
    }

    @Override
    public void save(User user) {

        checkUserCredentials(user);

        String salt = UUID.randomUUID().toString();
        String saltAndPassword = getHashPassword(salt.concat(user.getPassword()));

        user.setPassword(saltAndPassword);
        user.setSalt(salt);
        if (user.getLanguage() == null) {
            user.setLanguage(Language.ENGLISH);
        }

        userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }


    String getHashPassword(String saltAndPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = saltAndPassword.getBytes();
            for (int i = 0; i < iteration; i++) {
                digest.update(bytes);
                bytes = digest.digest();
            }
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot find algorithm -", e);
            throw new RuntimeException(e);
        }
    }

    private void checkUserCredentials(User user) {
        checkUserLogin(user.getLogin());
        if (user.getFirstName().length() > 40) {
            throw new IllegalArgumentException("Sorry, first name is too long. " +
                    "Please put first name up to 40 characters.");
        }

        if (user.getLastName().length() > 40) {
            throw new IllegalArgumentException("Sorry, last name is too long. " +
                    "Please put last name up to 40 characters.");
        }

        if (user.getEmail().length() > 50) {
            throw new IllegalArgumentException("Sorry, email is too long. " +
                    "Please put email up to 50 characters.");
        }

        if (user.getPassword().length() > 200) {
            throw new IllegalArgumentException("Sorry, password is too long. " +
                    "Please put password up to 200 characters.");
        }
    }

    private void checkUserLogin(String login) {
        if (login.length() > 50) {
            throw new IllegalArgumentException("Sorry, login is too long. " +
                    "Please put login up to 50 characters.");
        }
    }

}
