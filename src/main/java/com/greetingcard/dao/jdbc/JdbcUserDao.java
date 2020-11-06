package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class JdbcUserDao implements UserDao {
    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final String FIND_USER_BY_LOGIN = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id FROM users WHERE login = ?";
    private static final String SAVE_USER = "INSERT INTO users (firstName, lastName, login, email, password, salt, language_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET firstName=?, lastName=?, login=? WHERE user_id=?;";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User findByLogin(@NonNull String login) {
        log.info("Getting user by login {}", login);
        return jdbcTemplate.queryForObject(FIND_USER_BY_LOGIN, new Object[] {login}, USER_ROW_MAPPER);
    }

    @Override
    public User findUserByLogin(String login) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_LOGIN)) {

            preparedStatement.setString(1, login);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (!resultSet.next()) {
                    log.info("No user found for login: {}", login);
                    return null;
                }

                User user = USER_ROW_MAPPER.mapRow(resultSet);

                if (resultSet.next()) {
                    log.error("More than one user found for login: {}", login);
                    throw new RuntimeException("More than one user found for login: ".concat(login));
                }
                return user;
            }
        } catch (SQLException e) {
            log.error("Exception while getting user from DB: {}", login, e);
            throw new RuntimeException("Exception while getting user from DB: ".concat(login), e);
        }
    }

    public void saveUser(@NonNull User user) {
        jdbcTemplate.update(SAVE_USER, user.getFirstName(), user.getLastName(), user.getLogin(),
                user.getEmail(), user.getPassword(), user.getSalt(), user.getLanguage().getLanguageNumber());
        log.debug("Added new user {} to DB", user.getEmail());
    }

    @Override
    public void save(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_USER)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getLogin());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getSalt());
            statement.setDouble(7, user.getLanguage().getLanguageNumber());

            statement.execute();

        } catch (SQLException e) {
            log.error("Exception while save user to DB", e);
            throw new RuntimeException("Exception while save user to DB: ", e);
        }
    }

    public void editUser(@NonNull User user) {
        log.info("Edit user's (user_id:{}) personal information", user.getId());
        jdbcTemplate.update(UPDATE_USER, user.getFirstName(), user.getLastName(), user.getLogin(), user.getId());
    }

    @Override
    public void update(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getLogin());
            statement.setLong(4, user.getId());

            statement.execute();

        } catch (SQLException e) {
            log.error("Exception while update user", e);
            throw new RuntimeException("Exception while update user : ", e);
        }
    }
}
