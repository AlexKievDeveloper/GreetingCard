package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;

import java.util.Map;

public interface CardService {

    Map<Card, Role> getCards(long userId, String cardsType);

    void createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);
}
