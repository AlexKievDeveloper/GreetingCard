package com.greetingcard.security;

import com.greetingcard.dao.jdbc.JdbcUserDao;
import com.greetingcard.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

public class DefaultSecurityService implements SecurityService {

    private JdbcUserDao jdbcUserDao;

    public DefaultSecurityService(JdbcUserDao jdbcUserDao) {
        this.jdbcUserDao = jdbcUserDao;
    }

    @Override
    public User login(String login, String password) {
        User user = jdbcUserDao.findUserByLogin(login);
        if (user != null) {
            String salt = user.getSalt();
            String hashPassword = getHashPassword(salt, password);

            if (user.getPassword().equals(hashPassword)) {
                return user;
            }
        }
        return null;
    }

    String getHashPassword(String salt, String password) {
        return DigestUtils.sha256Hex(salt.concat(password));
    }
}
