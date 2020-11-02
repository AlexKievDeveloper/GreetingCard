package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;

import java.util.List;
import java.util.Map;

public interface CardService {

    List<Card> getCards(long userId, String cardsType);

    void createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatus(Status status, long cardId);
}
