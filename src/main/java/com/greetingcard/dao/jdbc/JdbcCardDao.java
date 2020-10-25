package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.jdbc.mapper.CardAndCongratulationRowMapper;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
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
    private static final String GET_CARDS_BY_USER_ID_AND_ROLE_ID = "SELECT cards.card_id, name, background_image, card_link, status_id, role_id" +
            " FROM cards LEFT JOIN users_cards ON cards.card_id=users_cards.card_id WHERE (user_id = ? AND role_id = ?) ORDER BY cards.card_id";
    private static final String SAVE_NEW_CARD = "INSERT INTO cards (name, status_id) VALUES (?,?)";
    private static final String ADD_TO_USERS_CARDS = "INSERT INTO users_cards (card_id, user_id, role_id) VALUES (?,?,?)";
    private static final String CARD_AND_CONGRATULATION = "SELECT cards.card_id, name, background_image, card_link, cards.status_id, congratulations.congratulation_id, congratulations.status_id as con_status, message, congratulations.user_id, firstName, lastName, login, link_id, link, type_id FROM cards LEFT JOIN congratulations ON cards.card_id=congratulations.card_id LEFT JOIN users ON congratulations.user_id=users.user_id LEFT JOIN links ON congratulations.congratulation_id=links.congratulation_id where cards.card_id = ?;";

    private static final CardRowMapper CARD_ROW_MAPPER = new CardRowMapper();
    private static final CardAndCongratulationRowMapper CARD_AND_CONGRATULATION_ROW_MAPPER = new CardAndCongratulationRowMapper();
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

    @Override
    public Map<Card, Role> getCardsByUserIdAndRoleId(int userId, int roleId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CARDS_BY_USER_ID_AND_ROLE_ID)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, roleId);

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
            log.error("Exception while getting my cards from DB by user id: {} and role id: {}", userId, roleId, e);
            throw new RuntimeException("Exception while getting my cards from DB by user id: " + userId +
                    " and role id: " + roleId, e);
        }
    }

    @Override
    public void createCard(Card card, User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementInCards = connection.prepareStatement(SAVE_NEW_CARD, PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            statementInCards.setString(1, card.getName());
            statementInCards.setInt(2, Status.STARTUP.getStatusNumber());
            statementInCards.execute();
            try (ResultSet resultSet = statementInCards.getGeneratedKeys()) {
                while (resultSet.next()) {
                    card.setId(resultSet.getInt(1));
                }
            }

            addNewCards(card, user, connection);

            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while creating new card", e);
            throw new RuntimeException("Exception while creating new card", e);
        }
    }

    @Override
    public Card getCardAndCongratulationByCardId(int cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CARD_AND_CONGRATULATION)) {
            statement.setInt(1, cardId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return CARD_AND_CONGRATULATION_ROW_MAPPER.mapRow(resultSet);
            }

        } catch (SQLException e) {
            log.error("Exception while get card and congratulation by card id", e);
            throw new RuntimeException("Exception while get card and congratulation by card id", e);
        }
    }

    private void addNewCards(Card card, User user, Connection connection) throws SQLException {
        try (PreparedStatement statementInUsers_Cards = connection.prepareStatement(ADD_TO_USERS_CARDS)) {
            statementInUsers_Cards.setInt(1, card.getId());
            statementInUsers_Cards.setInt(2, user.getId());
            statementInUsers_Cards.setInt(3, Role.ADMIN.getRoleNumber());
            statementInUsers_Cards.execute();
        }
    }
}
