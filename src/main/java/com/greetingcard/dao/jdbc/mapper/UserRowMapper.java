package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements ResultSetExtractor<User> {
    @Override
    public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        if (!resultSet.next()) {
            return null;
        }

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
