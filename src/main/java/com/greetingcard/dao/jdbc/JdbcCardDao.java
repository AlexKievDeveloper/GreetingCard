package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.jdbc.mapper.CardAndCongratulationExtractor;
import com.greetingcard.dao.jdbc.mapper.CardRowMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@PropertySource("classpath:queries.properties")
public class JdbcCardDao implements CardDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${get.cards.by.user.id.and.role.id}")
    private String getCardsByUserIdAndRoleId;
    @Value("${card.and.congratulation}")
    private String cardAndCongratulation;
    @Value("${get.card.status}")
    private String getCardStatus;
    @Value("${save.new.card}")
    private String saveNewCard;
    @Value("${add.to.users.cards}")
    private String addToUsersCards;
    @Value("${delete.by.card.id}")
    private String deleteByCardId;
    @Value("${change.status.of.card.by.id}")
    private String changeStatusOfCardById;
    @Value("${get.all.cards.by.user.id}")
    private String getAllCardsByUserId;
    @Value("${change.name}")
    private String changeName;

    public JdbcCardDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

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
    public Optional<Card> getCardAndCongratulationByCardId(long cardId, long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("cardId", cardId).addValue("userId", userId);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(cardAndCongratulation, namedParameters, new CardAndCongratulationExtractor()));
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        jdbcTemplate.update(deleteByCardId, cardId, userId);
    }

    @Override
    public void changeCardStatusById(Status newStatus, long cardId) {
        jdbcTemplate.update(changeStatusOfCardById, newStatus.getStatusNumber(), cardId);
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
}