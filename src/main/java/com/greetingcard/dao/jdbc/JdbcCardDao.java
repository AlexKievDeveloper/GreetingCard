package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.jdbc.mapper.CardAndCongratulationExtractor;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcCardDao implements CardDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private String getCardsByUserIdAndRoleId;
    @Autowired
    private String cardAndCongratulation;
    @Autowired
    private String getCardStatus;
    @Autowired
    private String saveNewCard;
    @Autowired
    private String addToUsersCards;
    @Autowired
    private String finishedCardAndCongratulation;
    @Autowired
    private String deleteByCardId;
    @Autowired
    private String changeStatusOfCardAndSetCardLinkById;
    @Autowired
    private String getAllCardsByUserId;
    @Autowired
    private String changeName;
    @Autowired
    private String saveBackground;
    @Autowired
    private String saveBackgroundOfCongratulations;
    @Autowired
    private String deleteBackground;
    @Autowired
    private String setTimeOfFinishCard;
    @Autowired
    private String finishCards;

    @Override
    public List<Card> getAllCardsByUserId(long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(getAllCardsByUserId, namedParameters, new CardRowMapper());
    }

    @Override
    public List<Card> getCardsByUserIdAndRoleId(long userId, long roleId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        namedParameters.addValue("roleId", roleId);
        return namedParameterJdbcTemplate.query(getCardsByUserIdAndRoleId, namedParameters, new CardRowMapper());
    }

    @Override
    @Transactional
    public Long createCard(Card card) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(saveNewCard, new String[]{"card_id"});
            preparedStatement.setLong(1, card.getUser().getId());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setInt(3, Status.STARTUP.getStatusNumber());
            return preparedStatement;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        jdbcTemplate.update(addToUsersCards, id, card.getUser().getId(), Role.ADMIN.getRoleNumber());
        return id;
    }

    @Override
    public Card getCardAndCongratulationByCardIdAndUserId(long cardId, long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("cardId", cardId).addValue("userId", userId);
        return namedParameterJdbcTemplate.query(cardAndCongratulation, namedParameters, new CardAndCongratulationExtractor());
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("cardId", cardId);
        return namedParameterJdbcTemplate.query(finishedCardAndCongratulation, namedParameters, new CardAndCongratulationExtractor());
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        jdbcTemplate.update(deleteByCardId, cardId, userId);
    }

    @Override
    public void changeCardStatusAndSetCardLinkById(Status newStatus, long cardId, String link) {
        jdbcTemplate.update(changeStatusOfCardAndSetCardLinkById, newStatus.getStatusNumber(), link, cardId);
    }

    @Override
    public Optional<Status> getCardStatusById(long cardId) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("card_id", cardId);
        List<Integer> statusIds = namedParameterJdbcTemplate.queryForList(getCardStatus, parameterSource, Integer.class);
        return (statusIds.size() != 0 ? Optional.of(Status.getByNumber(statusIds.get(0))) : Optional.empty());
    }

    @Override
    public void changeCardName(Card card) {
        jdbcTemplate.update(changeName, card.getName(), card.getId(), card.getUser().getId());
        log.info("Changed name of card to {} by id - {}", card.getName(), card.getId());
    }

    @Override
    public void saveBackground(long id, long user, String newName) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_id", id);
        map.put("user_id", user);
        map.put("background_image", newName);
        namedParameterJdbcTemplate.update(saveBackground, map);
    }

    @Override
    public void saveBackgroundOfCongratulation(long id, long user, String numberOfColor) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_id", id);
        map.put("user_id", user);
        map.put("background_congratulations", numberOfColor);
        namedParameterJdbcTemplate.update(saveBackgroundOfCongratulations, map);
    }

    @Override
    public void removeBackground(long id, long user) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_id", id);
        map.put("user_id", user);
        namedParameterJdbcTemplate.update(deleteBackground, map);
    }

    @Override
    public void setFinishTime(Card card) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("cardId", card.getId())
                .addValue("userId", card.getUser().getId())
                .addValue("dateOfFinish", card.getDateOfFinish());
        namedParameterJdbcTemplate.update(setTimeOfFinishCard, namedParameters);
    }

    @Override
    public void finishCards(LocalDate now) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("dateOfFinish", now)
                .addValue("statusId", Status.ISOVER.getStatusNumber());
        namedParameterJdbcTemplate.update(finishCards, namedParameters);
    }
}