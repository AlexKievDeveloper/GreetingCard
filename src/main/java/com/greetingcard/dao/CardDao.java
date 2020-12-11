package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;

import java.util.List;
import java.util.Optional;

public interface CardDao {
    List<Card> getAllCardsByUserId(long id);

    List<Card> getCardsByUserIdAndRoleId(long userId, long roleId);

    Long createCard(Card card);

    Optional<Card> getCardAndCongratulationByCardId(long cardId, long userId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatusById(Status status, long cardId);

    Optional<Status> getCardStatusById(long cardId);

    void changeCardName(Card card);
}
