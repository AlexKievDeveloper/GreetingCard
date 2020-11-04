package com.greetingcard.dao.jdbc;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CardAndCongratulationRowMapper;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcCardDao implements CardDao {
    private static final String GET_ALL_CARDS_BY_USER_ID = "SELECT cards.card_id, name, background_image, card_link, status_id, users.user_id, firstName, lastName, login, email FROM cards LEFT JOIN users_cards ON cards.card_id=users_cards.card_id LEFT JOIN users ON users_cards.user_id=users.user_id WHERE users.user_id = ? ORDER BY cards.card_id";
    private static final String GET_CARDS_BY_USER_ID_AND_ROLE_ID = "SELECT cards.card_id, name, background_image, card_link, status_id, users.user_id, firstName, lastName, login, email FROM cards LEFT JOIN users_cards ON cards.card_id=users_cards.card_id LEFT JOIN users ON users_cards.user_id=users.user_id WHERE (users.user_id = ? AND role_id = ?) ORDER BY cards.card_id";
    private static final String SAVE_NEW_CARD = "INSERT INTO cards (user_id, name, status_id) VALUES (?,?,?)";
    private static final String ADD_TO_USERS_CARDS = "INSERT INTO users_cards (card_id, user_id, role_id) VALUES (?,?,?)";
    private static final String CARD_AND_CONGRATULATION = "SELECT c.card_id ,c.user_id as card_user, name, background_image, card_link, c.status_id, cg.congratulation_id, cg.status_id as con_status, message, cg.user_id, firstName, lastName, login, link_id, link,type_id " +
            "FROM users_cards uc JOIN cards c ON uc.card_id = c.card_id LEFT JOIN congratulations cg ON c.card_id=cg.card_id LEFT JOIN users u ON cg.user_id=u.user_id LEFT JOIN links l ON cg.congratulation_id=l.congratulation_id WHERE uc.card_id = ? AND uc.user_id = ?";
    private static final String DELETE_BY_CARD_ID = "DELETE FROM cards WHERE card_id=? and user_id=?";
    private static final String CHANGE_STATUS_OF_CARD_BY_ID = "UPDATE cards SET status_id = ? where card_id = ?";

    private static final CardRowMapper CARD_ROW_MAPPER = new CardRowMapper();
    private static final CardAndCongratulationRowMapper CARD_AND_CONGRATULATION_ROW_MAPPER = new CardAndCongratulationRowMapper();
    private final CongratulationDao congratulationDao = ServiceLocator.getBean("JdbcCongratulationDao");
    private final DataSource dataSource;

    public JdbcCardDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Card> getAllCardsByUserId(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CARDS_BY_USER_ID)) {
            preparedStatement.setLong(1, id);
            List<Card> cardsList = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Card card = CARD_ROW_MAPPER.mapRow(resultSet);
                    cardsList.add(card);
                }
            }
            return cardsList;
        } catch (SQLException e) {
            log.error("Exception while getting cards from DB by user id: {}", id, e);
            throw new RuntimeException("Exception while getting cards from DB by user id: " + id, e);
        }
    }

    @Override
    public List<Card> getCardsByUserIdAndRoleId(long userId, long roleId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CARDS_BY_USER_ID_AND_ROLE_ID)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, roleId);
            List<Card> cardsList = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Card card = CARD_ROW_MAPPER.mapRow(resultSet);
                    cardsList.add(card);
                }
            }
            return cardsList;
        } catch (SQLException e) {
            log.error("Exception while getting my cards from DB by user id: {} and role id: {}", userId, roleId, e);
            throw new RuntimeException("Exception while getting my cards from DB by user id: " + userId +
                    " and role id: " + roleId, e);
        }
    }

    @Override
    public long createCard(Card card) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementInCards = connection.prepareStatement(SAVE_NEW_CARD, PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statementInCards.setLong(1, card.getUser().getId());
            statementInCards.setString(2, card.getName());
            statementInCards.setInt(3, Status.STARTUP.getStatusNumber());
            statementInCards.execute();
            long id = 0;
            try (ResultSet resultSet = statementInCards.getGeneratedKeys()) {
                while (resultSet.next()) {
                    id = resultSet.getInt(1);
                    card.setId(id);
                }
            }
            addNewCards(card, connection);
            connection.commit();
            return id;
        } catch (SQLException e) {
            log.error("Exception while creating new card", e);
            throw new RuntimeException("Exception while creating new card", e);
        }
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId, long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CARD_AND_CONGRATULATION)) {
            statement.setLong(1, cardId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return CARD_AND_CONGRATULATION_ROW_MAPPER.mapRow(resultSet);
            }

        } catch (SQLException e) {
            log.error("Exception while get card and congratulation by card id", e);
            throw new RuntimeException("Exception while get card and congratulation by card id", e);
        }
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_CARD_ID)) {
            connection.setAutoCommit(false);
            statement.setLong(1, cardId);
            statement.setLong(2, userId);
            congratulationDao.deleteByCardId(cardId, userId);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while deleting card by - {}", cardId, e);
            throw new RuntimeException("Exception while deleting card ", e);
        }
    }

    @Override
    public void changeCardStatusById(Status status, long cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHANGE_STATUS_OF_CARD_BY_ID)) {
            connection.setAutoCommit(false);
            statement.setInt(1, status.getStatusNumber());
            statement.setLong(2, cardId);
            statement.execute();
            try {
                congratulationDao.changeStatusCongratulationsByCardId(status, cardId);
            } catch (RuntimeException e) {
                connection.rollback();
                log.error("Exception while change status congratulation and card - {}", cardId, e);
                throw new RuntimeException("Exception while change status congratulation and card", e);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while change status card - {}", cardId, e);
            throw new RuntimeException("Exception while change status card ", e);
        }
    }

    void addNewCards(Card card, Connection connection) throws SQLException {
        try (PreparedStatement statementInUsers_Cards = connection.prepareStatement(ADD_TO_USERS_CARDS)) {
            statementInUsers_Cards.setLong(1, card.getId());
            statementInUsers_Cards.setLong(2, card.getUser().getId());
            statementInUsers_Cards.setInt(3, Role.ADMIN.getRoleNumber());
            statementInUsers_Cards.execute();
        }
    }
}
