package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.UserInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<UserInfo> {
    @Override
    public UserInfo mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return UserInfo.builder()
                .id(resultSet.getInt("user_id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .pathToPhoto(resultSet.getString("pathToPhoto"))
                .countCongratulations(resultSet.getInt("countCongratulations"))
                .build();
    }
}
