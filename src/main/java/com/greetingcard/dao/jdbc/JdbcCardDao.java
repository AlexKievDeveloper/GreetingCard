package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class JdbcCardDao implements CardDao {
    private static final String GET_ALL_CARDS_BY_USER_ID = "SELECT cards.card_id, name, background_image, card_link, status_id, role_id" +
            " FROM cards LEFT JOIN users_cards ON cards.card_id=users_cards.card_id WHERE user_id = ? ORDER BY cards.card_id";

    private static final CardRowMapper CARD_ROW_MAPPER = new CardRowMapper();
    private final DataSource dataSource;

    public JdbcCardDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<Card, Role> getAllCardsByUserId(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CARDS_BY_USER_ID)) {

            preparedStatement.setInt(1, id);

            Map<Card, Role> cards = new LinkedHashMap<>();

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    Card card = CARD_ROW_MAPPER.mapRow(resultSet);
                    Role role = Role.getByNumber(resultSet.getInt("role_id"));
                    cards.put(card, role);
                }
            }
            return cards;
        } catch (SQLException e) {
            log.error("Exception while getting cards from DB by user id: {}", id, e);
            throw new RuntimeException("Exception while getting cards from DB by user id: " + id, e);
        }
    }
}
