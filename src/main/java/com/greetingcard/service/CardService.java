package com.greetingcard.service;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
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

    void saveBackground(long id,User user,MultipartFile file);

    void saveBackgroundOfCongratulation(long id, User user, String color);

    void removeBackground(long id, User user);
}
