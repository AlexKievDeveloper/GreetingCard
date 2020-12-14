package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserFindByIdRowMapper;
import com.greetingcard.dao.jdbc.mapper.UserIdRowMapper;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.greetingcard.entity.AccessHashType.FORGOT_PASSWORD;
import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;

@Slf4j
@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JdbcUserDao implements UserDao {
    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private String saveUser;
    @Autowired
    private String updateUser;
    @Autowired
    private String updateUserPassword;
    @Autowired
    private String findUserById;
    @Autowired
    private String findUserByLogin;
    @Autowired
    private String findUserByEmail;
    @Autowired
    private String saveForgotPassAccessHash;
    @Autowired
    private String saveVerifyEmailAccessHash;
    @Autowired
    private String findUserByForgotPassAccessHash;
    @Autowired
    private String findVerifyEmailAccessHash;
    @Autowired
    private String deleteForgotPassAccessHash;
    @Autowired
    private String deleteVerifyEmailAccessHash;
    @Autowired
    private String updateUserVerifyEmail;

    @Override
    public void save(@NonNull User user) {
        jdbcTemplate.update(saveUser, user.getFirstName(), user.getLastName(), user.getLogin(),
                user.getEmail(), user.getPassword(), user.getSalt(), user.getLanguage().getLanguageNumber());
        log.debug("Added new user {} to DB", user.getEmail());
    }

    @Override
    public void update(@NonNull User user) {
        log.info("Edit user's (user_id:{}) personal information", user.getId());
        jdbcTemplate.update(updateUser, user.getFirstName(), user.getLastName(), user.getLogin(), user.getPathToPhoto(), user.getId());
    }

    @Override
    public void updatePassword(@NonNull User user) {
        log.info("Edit user's (user_id:{}) password", user.getId());
        jdbcTemplate.update(updateUserPassword, user.getPassword(), user.getId());
    }

    @Override
    public User findById(long id) {
        log.info("Getting user by login {}", id);
        return jdbcTemplate.queryForObject(findUserById, new UserFindByIdRowMapper(), id);
    }

    @Override
    public User findByLogin(@NonNull String login) {
        log.info("Getting user by login {}", login);
        return jdbcTemplate.query(findUserByLogin, USER_ROW_MAPPER, login);
    }

    @Override
    public User findByEmail(@NonNull String email) {
        log.info("Getting user by email {}", email);
        return jdbcTemplate.query(findUserByEmail, USER_ROW_MAPPER, email);
    }

    @Override
    public void saveAccessHash(@NonNull String email, @NonNull String hash, AccessHashType hashType) {
        User user = findByEmail(email);
        if (hashType == FORGOT_PASSWORD) {
            jdbcTemplate.update(saveForgotPassAccessHash, user.getId(), hash);
        }
        if (hashType == VERIFY_EMAIL) {
            jdbcTemplate.update(saveVerifyEmailAccessHash, user.getId(), hash);
        }
    }

    @Override
    @Transactional
    public void verifyEmailAccessHash(@NonNull String hash) {
        Long user_id = jdbcTemplate.query(findVerifyEmailAccessHash, new UserIdRowMapper(), hash);
        if (user_id != null) {
            jdbcTemplate.update(deleteVerifyEmailAccessHash, hash);
            jdbcTemplate.update(updateUserVerifyEmail, user_id);
        }
    }

    @Override
    public User findByForgotPasswordAccessHash(String hash) {
        log.debug("Getting user by access hash");
        return jdbcTemplate.query(findUserByForgotPassAccessHash, USER_ROW_MAPPER, hash);
    }

    @Override
    @Transactional
    public void verifyForgotPasswordAccessHash(@NonNull String hash, @NonNull User user) {
        jdbcTemplate.update(deleteForgotPassAccessHash, hash);
        jdbcTemplate.update(updateUserPassword, user.getPassword(), user.getId());
    }
}
