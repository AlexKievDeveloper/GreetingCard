package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Setter
public class JdbcCardDao implements CardDao {
    private JdbcTemplate jdbcTemplate;
    private CongratulationDao congratulationDao;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private TransactionTemplate transactionTemplate;

    @Override
    public List<Card> getAllCardsByUserId(long id) {
        return null;
    }

    @Override
    public List<Card> getCardsByUserIdAndRoleId(long userId, long roleId) {
        return null;
    }

    @Override
    public long createCard(Card card) {
        return 0;
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId, long userId) {
        return null;
    }

    @Override
    public void deleteCardById(long cardId, long userId) {

    }

    @Override
    public void changeCardStatusById(Status status, long cardId) {

    }
}
