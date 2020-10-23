package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;

import java.util.Map;

public interface CardService {

    Map<Card, Role> getCards(int userId, String cardsType);

    void createCard(Card card, User user);

    Card getCardAndCongratulationByCardId(int cardId);
}
