package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;

import java.util.List;

public interface CardService {

    List<Card> getCards(long userId, String cardsType);

    long createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatus(Status status, long cardId);
}
