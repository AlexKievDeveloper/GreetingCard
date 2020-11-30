package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CardAndCongratulationRowMapper;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Setter
public class JdbcCardDao implements CardDao {
    private static final String GET_ALL_CARDS_BY_USER_ID =
            "SELECT cards.card_id, " +
                    "name, " +
                    "background_image, " +
                    "card_link, status_id, " +
                    "users.user_id, " +
                    "firstName, " +
                    "lastName, " +
                    "login, " +
                    "email " +
                    "FROM cards " +
                    "LEFT JOIN users_cards ON (cards.card_id=users_cards.card_id) " +
                    "LEFT JOIN users ON (users_cards.user_id=users.user_id) " +
                    "WHERE users.user_id = :id " +
                    "ORDER BY cards.card_id";
    private static final String GET_CARDS_BY_USER_ID_AND_ROLE_ID =
            "SELECT cards.card_id, " +
                    "name, " +
                    "background_image, " +
                    "card_link, " +
                    "status_id, " +
                    "users.user_id, " +
                    "firstName, " +
                    "lastName, " +
                    "login, " +
                    "email " +
                    "FROM cards " +
                    "LEFT JOIN users_cards ON (cards.card_id=users_cards.card_id) " +
                    "LEFT JOIN users ON (users_cards.user_id=users.user_id) " +
                    "WHERE (users.user_id = :userId AND role_id = :roleId) " +
                    "ORDER BY cards.card_id";
    private static final String GET_CARD_STATUS = "SELECT status_id FROM cards WHERE card_id = :card_id";
    private static final String SAVE_NEW_CARD = "INSERT INTO cards (user_id, name, status_id) VALUES (?,?,?)";
    private static final String ADD_TO_USERS_CARDS = "INSERT INTO users_cards (card_id, user_id, role_id) VALUES (?,?,?)";
    private static final String CARD_AND_CONGRATULATION =
            "SELECT c.card_id, " +
                    "c.user_id as card_user, " +
                    "name, " +
                    "background_image, " +
                    "card_link, " +
                    "c.status_id, " +
                    "cg.congratulation_id, " +
                    "cg.status_id as con_status, " +
                    "message, " +
                    "cg.user_id, " +
                    "firstName, " +
                    "lastName, " +
                    "login, " +
                    "link_id, " +
                    "link,type_id " +
                    "FROM users_cards uc " +
                    "JOIN cards c ON (uc.card_id = c.card_id) " +
                    "LEFT JOIN congratulations cg ON (c.card_id=cg.card_id) " +
                    "LEFT JOIN users u ON (cg.user_id=u.user_id) " +
                    "LEFT JOIN links l ON (cg.congratulation_id=l.congratulation_id) " +
                    "WHERE uc.card_id = :cardId AND uc.user_id = :userId";
    private static final String DELETE_BY_CARD_ID = "DELETE FROM cards WHERE card_id=? and user_id=?";
    private static final String CHANGE_STATUS_OF_CARD_BY_ID = "UPDATE cards SET status_id = ? where card_id = ?";

    private CongratulationDao congratulationDao;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private TransactionTemplate transactionTemplate;

    public JdbcCardDao(CongratulationDao congratulationDao, JdbcTemplate jdbcTemplate,
                       NamedParameterJdbcTemplate namedParameterJdbcTemplate, TransactionTemplate transactionTemplate) {
        this.congratulationDao = congratulationDao;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<Card> getAllCardsByUserId(long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(GET_ALL_CARDS_BY_USER_ID, namedParameters, new CardRowMapper());
    }

    @Override
    public List<Card> getCardsByUserIdAndRoleId(long userId, long roleId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        namedParameters.addValue("roleId", roleId);
        return namedParameterJdbcTemplate.query(GET_CARDS_BY_USER_ID_AND_ROLE_ID, namedParameters, new CardRowMapper());
    }

    @Override
    public Long createCard(Card card) {
        return transactionTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_NEW_CARD, new String[]{"card_id"});
                preparedStatement.setLong(1, card.getUser().getId());
                preparedStatement.setString(2, card.getName());
                preparedStatement.setInt(3, Status.STARTUP.getStatusNumber());
                return preparedStatement;
            }, keyHolder);
            long id = keyHolder.getKey().longValue();
            jdbcTemplate.update(ADD_TO_USERS_CARDS, id, card.getUser().getId(), Role.ADMIN.getRoleNumber());
            return id;
        });
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId, long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("cardId", cardId).addValue("userId", userId);
        return namedParameterJdbcTemplate.query(CARD_AND_CONGRATULATION, namedParameters, new CardAndCongratulationRowMapper());
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                congratulationDao.deleteByCardId(cardId, userId);
                jdbcTemplate.update(DELETE_BY_CARD_ID, cardId, userId);
            }
        });
    }

    @Override
    public void changeCardStatusById(Status newStatus, long cardId) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jdbcTemplate.update(CHANGE_STATUS_OF_CARD_BY_ID, newStatus.getStatusNumber(), cardId);
                congratulationDao.changeStatusCongratulationsByCardId(newStatus, cardId);
            }
        });
    }

    public Optional<Status> getCardStatusById(long cardId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("card_id", cardId);
        List<Integer> statusIds = namedParameterJdbcTemplate.queryForList(GET_CARD_STATUS, parameterSource, Integer.class);
        return (statusIds.size() != 0 ? Optional.of(Status.getByNumber(statusIds.get(0))) : Optional.empty());
    }
}