package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CardRowMapper implements RowMapper<Card> {
    @Override
    public Card mapRow(ResultSet resultSet, int rowNum) throws SQLException {
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
                .backgroundCongratulations( resultSet.getString("background_congratulations"))
                .cardLink(resultSet.getString("card_link"))
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();
    }

}
