package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.mapper.UserRowMapper;
import com.greetingcard.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class JdbcUserDao implements UserDao {
    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final String FIND_USER_BY_LOGIN = "SELECT user_id, firstName, lastName, login, email, password, salt, language_id FROM users WHERE login = ?";
    private static final String SAVE_USER = "INSERT INTO users (firstName, lastName, login, email, password, salt, language_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET firstName=?, lastName=?, login=? WHERE user_id=?;";

    private final DataSource dataSource;

    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
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
