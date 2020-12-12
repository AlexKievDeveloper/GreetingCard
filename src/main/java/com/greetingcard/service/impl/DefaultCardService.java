package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CongratulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultCardService implements CardService {
    @Autowired
    private CardDao cardDao;
    @Autowired
    private CongratulationService congratulationService;

    @Override
    public List<Card> getCards(long userId, CardsType cardsType) {
        switch (cardsType) {
            case All:
                return cardDao.getAllCardsByUserId(userId);
            case MY:
                return cardDao.getCardsByUserIdAndRoleId(userId, 1);
            case OTHER:
                return cardDao.getCardsByUserIdAndRoleId(userId, 2);
        }
        return List.of();
    }

    @Override
    public Long createCard(Card card) {
        return cardDao.createCard(card);
    }

    @Override
    public Optional<Card> getCardAndCongratulationByCardId(long cardId, long userId) {
        return cardDao.getCardAndCongratulationByCardId(cardId, userId);
    }

    @Override
    @Transactional
    public void deleteCardById(long cardId, long userId) {
        cardDao.deleteCardById(cardId, userId);
        congratulationService.deleteByCardId(cardId, userId);
    }

    @Override
    @Transactional
    public void changeCardStatus(Status status, long cardId) {
        cardDao.changeCardStatusById(status, cardId);
        congratulationService.changeCongratulationStatusByCardId(status, cardId);
    }

    @Override
    public void changeCardName(Card card) {
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        cardDao.changeCardName(card);
    }

    @Override
    public Optional<Status> getCardStatusById(long cardId) {
        return cardDao.getCardStatusById(cardId);
    }

}
