package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.*;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CongratulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultCardService implements CardService {
    private final CardDao cardDao;
    private final CongratulationService congratulationService;
    private final DefaultAmazonService defaultAmazonService;

    @Override
    public List<Card> getCards(long userId, CardsType cardsType) {
        switch (cardsType) {
            case ALL:
                return cardDao.getAllCardsByUserId(userId);
            case MY:
                return cardDao.getCardsByUserIdAndRoleId(userId, 1);
            case OTHER:
                return cardDao.getCardsByUserIdAndRoleId(userId, 2);
        }
        return List.of();
    }

    @Override
    public Long createCard(Card card) {
        return cardDao.createCard(card);
    }

    @Override
    public Card getCardAndCongratulationByCardIdAndUserId(long cardId, long userId) {
        return cardDao.getCardAndCongratulationByCardIdAndUserId(cardId, userId);
    }

    @Override
    public Card getCardAndCongratulationByCardId(long cardId) {
        return cardDao.getCardAndCongratulationByCardId(cardId);
    }

    @Override
    public void deleteCardById(long cardId, long userId) {
        cardDao.deleteCardById(cardId, userId);
    }

    @Override
    @Transactional
    public void changeCardStatusAndCreateCardLink(String statusName, long cardId) {
        Status status = Status.getByName(statusName);
        String hash = UUID.randomUUID().toString().replaceAll("/", "");
        cardDao.changeCardStatusAndSetCardLinkById(status, cardId, hash);
        congratulationService.changeCongratulationStatusByCardId(status, cardId);
    }

    @Override
    public void changeCardName(Card card) {
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is empty or too long");
        }
        cardDao.changeCardName(card);
    }

    @Override
    @Transactional
    public void saveBackground(long id, long user, MultipartFile image) {
        String pathToStorage = "background/";
        String newName = pathToStorage.concat(UUID.randomUUID().toString());
        String contentType = image.getContentType();

        if (LinkType.PICTURE.getAdditionalTypes().contains(contentType)) {
            cardDao.saveBackground(id, user, "/"+newName);
            defaultAmazonService.uploadFile(image, newName);
        } else {
            throw new IllegalArgumentException("Sorry, this format is not supported by the application: "
                    .concat(contentType));
        }
    }

    @Override
    public void saveBackgroundOfCongratulation(long id, long user, String numberOfColor) {
        cardDao.saveBackgroundOfCongratulation(id, user, numberOfColor);
    }

    @Override
    public void removeBackground(long id, long user) {
        cardDao.removeBackground(id,user);
    }

    @Override
    public Optional<Status> getCardStatusById(long cardId) {
        return cardDao.getCardStatusById(cardId);
    }

}
