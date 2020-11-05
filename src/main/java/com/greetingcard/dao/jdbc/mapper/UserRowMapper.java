package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {
    public User mapRow(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .salt(resultSet.getString("salt"))
                .language(Language.getByNumber(resultSet.getInt("language_id")))
                .build();
    }
}

