package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultCardService implements CardService {
    @Autowired
    private CardDao cardDao;

    @Override
    public List<Card> getCards(long userId, String cardsType) {

        switch (cardsType) {
            case "all":
                return cardDao.getAllCardsByUserId(userId);
            case "my":
                return cardDao.getCardsByUserIdAndRoleId(userId, 1);
            case "other":
                return cardDao.getCardsByUserIdAndRoleId(userId, 2);
            default:
                return null;
        }
    }

    @Override
    public Long createCard(Card card) {
        return cardDao.createCard(card);
    }

    @Override
    public Card getCardAndCongratulationByCardIdAndUserId(long cardId, long userId) {
        return cardDao.getCardAndCongratulationByCardIdAndUserId(cardId, userId);
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId) {
        return cardDao.getCardAndCongratulationByCardId(cardId);
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        cardDao.deleteCardById(cardId, userId);
    }

    @Override
    public void changeCardStatusAndCreateCardLink(String statusName, long cardId) {
        Status status = Status.getByName(statusName);
        String hash = UUID.randomUUID().toString().replaceAll("/", "");
        cardDao.changeCardStatusAndSetCardLinkById(status, cardId, hash);
//    public void changeCardStatus(String statusName, long cardId) {
//        Status status = Status.getByName(statusName);
//        cardDao.changeCardStatusById(status, cardId);
    }

    @Override
    public void changeCardName(Card card) {
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        cardDao.changeCardName(card);
    }

    public Optional<Status> getCardStatusById(long cardId) {
        return cardDao.getCardStatusById(cardId);
    }

}
