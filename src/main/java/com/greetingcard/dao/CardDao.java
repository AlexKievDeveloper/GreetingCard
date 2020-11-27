package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;

import java.util.List;

public interface CardDao {
    List<Card> getAllCardsByUserId(long id);

    List<Card> getCardsByUserIdAndRoleId(long userId, long roleId);

    Long createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatusById(Status status, long cardId);
}
