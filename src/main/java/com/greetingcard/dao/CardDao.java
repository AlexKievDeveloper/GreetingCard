package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CardDao {
    List<Card> getAllCardsByUserId(long id);

    List<Card> getCardsByUserIdAndRoleId(long userId, long roleId);

    Long createCard(Card card);

    Card getCardAndCongratulationByCardIdAndUserId(long cardId, long userId);

    Card getCardAndCongratulationByCardId(long cardId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatusAndSetCardLinkById(Status status, long cardId, String link);

    Optional<Status> getCardStatusById(long cardId);

    void changeCardName(Card card);

    void saveBackground(long id, long user, String newName);

    void saveBackgroundOfCongratulation(long id, long user, String numberOfColor);

    void removeBackground(long id, long user);

    void setFinishTime(Card card);

    void finishCards(LocalDate now);
}
