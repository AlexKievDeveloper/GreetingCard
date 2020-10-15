package com.greetingcard.security;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.jdbc.JdbcUserDao;
import com.greetingcard.entity.User;
import com.greetingcard.util.PropertyReader;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultSecurityService implements SecurityService, Runnable {

    private List<Session> sessionList = new CopyOnWriteArrayList<>();
    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private JdbcUserDao jdbcUserDao;

    public DefaultSecurityService(JdbcUserDao jdbcUserDao) {
        this.jdbcUserDao = jdbcUserDao;
    }

    @Override
    public Session login(String login, String password) {
        User user = jdbcUserDao.findUserByLogin(login);
        if (user != null) {
            String salt = user.getSalt();
            String hashPassword = getHashPassword(salt, password);
            long sessionMaxAge = Long.parseLong(propertyReader.getProperty("session.max-age"));

            if (user.getPassword().equals(hashPassword)) {
                Session session = Session.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expireDate(LocalDateTime.now().plusSeconds(sessionMaxAge))
                        .build();
                sessionList.add(session);
                return session;
            }
        }
        return null;
    }

    @Override
    public void logout(String token) {
        sessionList.removeIf(session -> session.getToken().equals(token));
    }

    @Override
    public Session getSession(String token) {
        for (Session session : sessionList) {
            if (session.getToken().equals(token)) {
                if (session.getExpireDate().isBefore(LocalDateTime.now())) {
                    sessionList.remove(session);
                    return null;
                }
                return session;
            }
        }
        return null;
    }

    String getHashPassword(String salt, String password) {
        return DigestUtils.sha256Hex(salt.concat(password));
    }

    @Override
    public void run() {
        sessionList.removeIf(session -> session.getExpireDate().isBefore(LocalDateTime.now()));
    }

    void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }
}
