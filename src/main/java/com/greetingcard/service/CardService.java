package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CardService {

    List<Card> getCards(long userId, CardsType cardsType);

    Long createCard(Card card);

    Card getCardAndCongratulationByCardIdAndUserId(long cardId, long userId);

    Card getCardAndCongratulationByCardId(long cardId);

    Optional<Status> getCardStatusById(long cardId);

    void deleteCardById(long cardId, long userId);

    void changeCardStatusAndCreateCardLink(String statusName, long cardId);

    void changeCardName(Card card);

    void saveBackground(long id, long user, MultipartFile file);

    void saveBackgroundOfCongratulation(long id, long user, String color);

    void removeBackground(long id, long user);

    void setFinishTime(Card card);
}
