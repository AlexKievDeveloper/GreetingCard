package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserIdExtractor;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.greetingcard.entity.AccessHashType.FORGOT_PASSWORD;
import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;

@Slf4j
@RequiredArgsConstructor
@Repository
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JdbcUserDao implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private String saveUser;
    @Autowired
    private String updateUser;
    @Autowired
    private String updateUserPassword;
    @Autowired
    private String updateUserLanguage;
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
    @Autowired
    private String saveUserFromFacebook;
    @Autowired
    private String saveUserFromGoogle;

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
    public void updateLanguage(User user) {
        log.info("Update user's (user_id:{}) language to {}", user.getId(), user.getLanguage().getName());
        jdbcTemplate.update(updateUserLanguage, user.getLanguage().getLanguageNumber(), user.getId());
    }

    @Override
    public User findByLogin(@NonNull String login) {
        log.info("Getting user by login {}", login);
        return jdbcTemplate.queryForObject(findUserByLogin, new UserRowMapper(), login);
    }

    @Override
    public User findByEmail(@NonNull String email) {
        log.info("Getting user by email {}", email);
        try {
            return jdbcTemplate.queryForObject(findUserByEmail, new UserRowMapper(), email);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("User with email: ".concat(email).concat(" does not exist"), e);
        }
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
        Long user_id = jdbcTemplate.query(findVerifyEmailAccessHash, new UserIdExtractor(), hash);
        if (user_id != null) {
            jdbcTemplate.update(deleteVerifyEmailAccessHash, hash);
            jdbcTemplate.update(updateUserVerifyEmail, user_id);
        }
    }

    @Override
    public User findByForgotPasswordAccessHash(String hash) {
        try {
            log.debug("Getting user by access hash");
            return jdbcTemplate.queryForObject(findUserByForgotPassAccessHash, new UserRowMapper(), hash);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("No user found for requested hash");
        }
    }

    @Override
    @Transactional
    public void verifyForgotPasswordAccessHash(@NonNull String hash, @NonNull User user) {
        jdbcTemplate.update(deleteForgotPassAccessHash, hash);
        jdbcTemplate.update(updateUserPassword, user.getPassword(), user.getId());
    }

    @Override
    public long saveUserFromFacebook(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("facebookId", user.getFacebook())
                .addValue("password", user.getPassword())
                .addValue("salt", user.getSalt())
                .addValue("language", user.getLanguage().getLanguageNumber());

        return namedParameterJdbcTemplate.update(saveUserFromFacebook, namedParameters, keyHolder);
    }

    @Override
    public long saveUserFromGoogle(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("google", user.getGoogle())
                .addValue("password", user.getPassword())
                .addValue("salt", user.getSalt())
                .addValue("language", user.getLanguage().getLanguageNumber())
                .addValue("pathToPhoto", user.getPathToPhoto());

        return namedParameterJdbcTemplate.update(saveUserFromGoogle, namedParameters, keyHolder);

    }
}