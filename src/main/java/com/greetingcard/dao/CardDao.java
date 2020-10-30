package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;

import java.util.Map;

public interface CardDao {
    Map<Card, Role> getAllCardsByUserId(long id);

    Map<Card, Role> getCardsByUserIdAndRoleId(long userId, long roleId);

    void createCard(Card card);

    Card getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);

    void changeStatusCardById(Status status, long cardId);
}
