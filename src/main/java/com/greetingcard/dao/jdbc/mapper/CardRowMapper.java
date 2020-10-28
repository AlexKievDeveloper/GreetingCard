package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CardRowMapper {
    public Card mapRow(ResultSet resultSet) throws SQLException {
        User user = User.builder()
                .id(resultSet.getLong("user_id"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .build();
        return Card.builder()
                .id(resultSet.getLong("card_id"))
                .user(user)
                .name(resultSet.getString("name"))
                .backgroundImage(resultSet.getString("background_image"))
                .cardLink(resultSet.getString("card_link"))
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();
    }
}
