package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;

import java.util.List;
import java.util.Map;

public interface CardDao {
    List<Card> getAllCardsByUserId(long id);

    List<Card> getCardsByUserIdAndRoleId(long userId, long roleId);

    void createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);
}
