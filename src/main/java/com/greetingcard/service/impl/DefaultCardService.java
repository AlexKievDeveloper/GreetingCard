package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import lombok.Setter;

import java.util.List;

@Setter
public class DefaultCardService implements CardService {
    private CardDao cardDao;

    @Override
    public List<Card> getCards(long userId, String cardsType) {
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
    public void changeCardStatus(Status status, long cardId) {

    }
}
