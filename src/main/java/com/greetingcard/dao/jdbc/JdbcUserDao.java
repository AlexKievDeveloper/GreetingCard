package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserFindByIdRowMapper;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

@Slf4j
@Setter
public class JdbcUserDao implements UserDao {
    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final String SAVE_USER = "INSERT INTO users (firstName, lastName, login, email, password, salt, language_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET firstName=?, lastName=?, login=? WHERE user_id=?;";
    private static final String UPDATE_USER_PASSWORD = "UPDATE users SET password=? WHERE user_id=?;";
    private static final String FIND_USER_BY_ID = "SELECT user_id, firstName, lastName, login, email, language_id FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_LOGIN = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id FROM users WHERE login = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id FROM users WHERE email = ?";
    private static final String FIND_ACCESS_HASH = "SELECT user_id, firstName, lastName, login, email, language_id FROM users LEFT JOIN ? AS hashes ON(users.user_id = hashes.user_id) WHERE hash = ?";
    private static final String INSERT_ACCESS_HASH = "INSERT INTO ? (user_id, hash) VALUES (?, ?)";

    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(@NonNull User user) {
        jdbcTemplate.update(SAVE_USER, user.getFirstName(), user.getLastName(), user.getLogin(),
                user.getEmail(), user.getPassword(), user.getSalt(), user.getLanguage().getLanguageNumber());
        log.debug("Added new user {} to DB", user.getEmail());
    }

    @Override
    public void update(@NonNull User user) {
        log.info("Edit user's (user_id:{}) personal information", user.getId());
        jdbcTemplate.update(UPDATE_USER, user.getFirstName(), user.getLastName(), user.getLogin(), user.getId());
    }

    @Override
    public void updatePassword(@NonNull User user) {
        log.info("Edit user's (user_id:{}) password", user.getId());
        jdbcTemplate.update(UPDATE_USER_PASSWORD, user.getId(), user.getPassword());
    }

    @Override
    public User findById(long id) {
        log.info("Getting user by login {}", id);
        return jdbcTemplate.queryForObject(FIND_USER_BY_ID, new UserFindByIdRowMapper(), id);
    }

    @Override
    public User findByLogin(@NonNull String login) {
        log.info("Getting user by login {}", login);
        return jdbcTemplate.query(FIND_USER_BY_LOGIN, USER_ROW_MAPPER, login);
    }

    @Override
    public User findByEmail(@NonNull String email) {
        log.info("Getting user by email {}", email);
        return jdbcTemplate.query(FIND_USER_BY_EMAIL, USER_ROW_MAPPER, email);
    }

    @Override
    public void saveAccessHash(String email, String hash, AccessHashType hashType) {
        User user = findByEmail(email);
        jdbcTemplate.update(INSERT_ACCESS_HASH, hashType.getTableName(), user.getId(), hash);
    }

    @Override
    public void checkAccessHash(String hash, AccessHashType hashType) {
        jdbcTemplate.query(FIND_ACCESS_HASH, USER_ROW_MAPPER, hashType.getTableName(), hash);
    }
}
