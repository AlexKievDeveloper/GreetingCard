package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserFindByIdRowMapper;
import com.greetingcard.dao.jdbc.mapper.UserIdRowMapper;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import static com.greetingcard.entity.AccessHashType.FORGOT_PASSWORD;
import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;


@Slf4j
@Setter
public class JdbcUserDao implements UserDao {
    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final String SAVE_USER = "INSERT INTO users (firstName, lastName, login, email, password, salt, language_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET firstName=?, lastName=?, login=?, pathToPhoto=COALESCE(?, pathToPhoto) WHERE user_id=?;";
    private static final String UPDATE_USER_PASSWORD = "UPDATE users SET password=? WHERE user_id=?;";
    private static final String FIND_USER_BY_ID = "SELECT user_id, firstName, lastName, login, email, language_id, facebook, google, pathToPhoto FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_LOGIN = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id, facebook, google, pathToPhoto FROM users WHERE login = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id, facebook, google, pathToPhoto FROM users WHERE email = ?";
    private static final String SAVE_FORGOT_PASS_ACCESS_HASH = "INSERT INTO forgot_password_hashes (user_id, hash) VALUES (?, ?)";
    private static final String SAVE_VERIFY_EMAIL_ACCESS_HASH = "INSERT INTO verify_email_hashes (user_id, hash) VALUES (?, ?)";
    private static final String FIND_FORGOT_PASS_ACCESS_HASH = "SELECT user_id FROM forgot_password_hashes WHERE hash = ?";
    private static final String FIND_VERIFY_EMAIL_ACCESS_HASH = "SELECT user_id FROM verify_email_hashes WHERE hash = ?";
    private static final String DELETE_FORGOT_PASS_ACCESS_HASH = "DELETE FROM forgot_password_hashes WHERE hash = ?";
    private static final String DELETE_VERIFY_EMAIL_ACCESS_HASH = "DELETE FROM verify_email_hashes WHERE hash = ?";
    private static final String UPDATE_USER_VERIFY_EMAIL = "UPDATE users SET email_verified='1' WHERE user_id=?;";

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
        jdbcTemplate.update(UPDATE_USER, user.getFirstName(), user.getLastName(), user.getLogin(), user.getPathToPhoto(), user.getId());
    }

    @Override
    public void updatePassword(@NonNull User user) {
        log.info("Edit user's (user_id:{}) password", user.getId());
        jdbcTemplate.update(UPDATE_USER_PASSWORD, user.getPassword(), user.getId());
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
        if (hashType == FORGOT_PASSWORD) {
            jdbcTemplate.update(SAVE_FORGOT_PASS_ACCESS_HASH, user.getId(), hash);
        }
        if (hashType == VERIFY_EMAIL) {
            jdbcTemplate.update(SAVE_VERIFY_EMAIL_ACCESS_HASH, user.getId(), hash);
        }
    }

    @Override
    @Transactional
    public Boolean verifyEmailAccessHash(String hash) {
        User user = jdbcTemplate.query(FIND_VERIFY_EMAIL_ACCESS_HASH, new UserIdRowMapper(), hash);
        if (user != null) {
            jdbcTemplate.update(DELETE_VERIFY_EMAIL_ACCESS_HASH, hash);
            jdbcTemplate.update(UPDATE_USER_VERIFY_EMAIL, user.getId());
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Boolean verifyForgotPasswordAccessHash(String hash, String password) {
        User user = jdbcTemplate.query(FIND_FORGOT_PASS_ACCESS_HASH, new UserIdRowMapper(), hash);
        if (user != null) {
            jdbcTemplate.update(DELETE_FORGOT_PASS_ACCESS_HASH, hash);
            jdbcTemplate.update(UPDATE_USER_PASSWORD, password, user.getId());
            return true;
        }

        return false;
    }
}
