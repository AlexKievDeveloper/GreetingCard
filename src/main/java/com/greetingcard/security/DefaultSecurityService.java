package com.greetingcard.security;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.jdbc.JdbcUserDao;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.util.PropertyReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class DefaultSecurityService implements SecurityService {

//    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    @Autowired
    private PropertyReader propertyReader;
    @Autowired
    private JdbcUserDao jdbcUserDao;

    public DefaultSecurityService(JdbcUserDao jdbcUserDao) {
        this.jdbcUserDao = jdbcUserDao;
    }

    @Override
    public User login(String login, String password) {
        log.info("login: {}", login);
        User user = jdbcUserDao.findByLogin(login);

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
        String salt = UUID.randomUUID().toString();
        String saltAndPassword = getHashPassword(salt.concat(user.getPassword()));

        user.setPassword(saltAndPassword);
        user.setSalt(salt);
        if (user.getLanguage() == null) {
            user.setLanguage(Language.ENGLISH);
        }

        jdbcUserDao.save(user);
    }

    @Override
    public void update(User user) {
        jdbcUserDao.update(user);
    }

    String getHashPassword(String saltAndPassword) {
        String algorithm = propertyReader.getProperty("algorithm");
        int iteration = Integer.parseInt(propertyReader.getProperty("iteration"));

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

}
