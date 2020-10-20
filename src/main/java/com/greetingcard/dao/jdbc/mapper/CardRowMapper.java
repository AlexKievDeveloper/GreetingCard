package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CardRowMapper {
    public Card mapRow(ResultSet resultSet) throws SQLException {
        return Card.builder()
                .id(resultSet.getInt("card_id"))
                .name(resultSet.getString("name"))
                .backgroundImage(resultSet.getString("background_image"))
                .cardLink(resultSet.getString("card_link"))
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();
    }
}
