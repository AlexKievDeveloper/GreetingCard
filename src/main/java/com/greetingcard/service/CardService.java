package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;

import java.util.List;
import java.util.Optional;

public interface CardService {

    List<Card> getCards(long userId, CardsType cardsType);

    Long createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    Optional<Status> getCardStatusById(long cardId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatus(Status status, long cardId);

    void changeCardName(Card card);
}
