package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultCardService implements CardService {
    private final CardDao jdbcCardDao;

    @Autowired
    public DefaultCardService(CardDao jdbcCardDao) {
        this.jdbcCardDao = jdbcCardDao;
    }

    @Override
    public List<Card> getCards(long userId, String cardsType) {

        switch (cardsType) {
            case "all":
                return jdbcCardDao.getAllCardsByUserId(userId);
            case "my":
                return jdbcCardDao.getCardsByUserIdAndRoleId(userId, 1);
            case "other":
                return jdbcCardDao.getCardsByUserIdAndRoleId(userId, 2);
            default:
                return null;
        }
    }

    @Override
    public long createCard(Card card) {
        return jdbcCardDao.createCard(card);
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId, long userId) {
        return jdbcCardDao.getCardAndCongratulationByCardId(cardId, userId);
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        jdbcCardDao.deleteCardById(cardId, userId);
    }

    @Override
    public void changeCardStatus(Status status, long cardId) {
        jdbcCardDao.changeCardStatusById(status, cardId);
    }
}