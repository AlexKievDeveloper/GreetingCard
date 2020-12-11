package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CongratulationService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DefaultCardService implements CardService {
    private final CardDao jdbcCardDao;
    private final CongratulationService congratulationService;

    @Override
    public List<Card> getCards(long userId, CardsType cardsType) {
        switch (cardsType) {
            case All:
                return jdbcCardDao.getAllCardsByUserId(userId);
            case MY:
                return jdbcCardDao.getCardsByUserIdAndRoleId(userId, 1);
            case OTHER:
                return jdbcCardDao.getCardsByUserIdAndRoleId(userId, 2);
        }
        return List.of();
    }

    @Override
    public Long createCard(Card card) {
        return jdbcCardDao.createCard(card);
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId, long userId) {
        return jdbcCardDao.getCardAndCongratulationByCardId(cardId, userId);
    }

    @Override
    @Transactional
    public void deleteCardById(long cardId, long userId) {
        jdbcCardDao.deleteCardById(cardId, userId);
        congratulationService.deleteByCardId(cardId, userId);
    }

    @Override
    @Transactional
    public void changeCardStatus(Status status, long cardId) {
        jdbcCardDao.changeCardStatusById(status, cardId);
        congratulationService.changeCongratulationStatusByCardId(status, cardId);
    }

    @Override
    public void changeCardName(Card card) {
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        jdbcCardDao.changeCardName(card);
    }

    @Override
    public Optional<Status> getCardStatusById(long cardId) {
        return jdbcCardDao.getCardStatusById(cardId);
    }

}
