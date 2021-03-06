package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int row) throws SQLException, DataAccessException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .salt(resultSet.getString("salt"))
                .language(Language.getByNumber(resultSet.getInt("language_id")))
                .google(resultSet.getString("google"))
                .facebook(resultSet.getString("facebook"))
                .pathToPhoto(resultSet.getString("pathToPhoto"))
                .build();
    }
}
