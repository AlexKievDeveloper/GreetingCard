package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.CardsType;
import com.greetingcard.service.CongratulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultCardServiceTest {
    @Mock
    private CardDao jdbcCardDao;
    @Mock
    private CongratulationService service;
    @InjectMocks
    private DefaultCardService defaultCardService;

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsAllCards() {
        //when
        defaultCardService.getCards(1, CardsType.ALL);
        //then
        verify(jdbcCardDao).getAllCardsByUserId(1);
    }

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsMyCards() {
        //when
        defaultCardService.getCards(1, CardsType.MY);
        //then
        verify(jdbcCardDao).getCardsByUserIdAndRoleId(1, 1);
    }

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsAnotherCards() {
        //when
        defaultCardService.getCards(1, CardsType.OTHER);
        //then
        verify(jdbcCardDao).getCardsByUserIdAndRoleId(1, 2);
    }

    @Test
    @DisplayName("Return List with all congratulations")
    void getCardAndCongratulationByCardId() {
        //when
        defaultCardService.getCardAndCongratulationByCardIdAndUserId(1, 1);
        //then
        verify(jdbcCardDao).getCardAndCongratulationByCardIdAndUserId(1, 1);
    }

    @Test
    @DisplayName("Changing card status to STARTUP and creating card link(hash)")
    void changeCardStatusAndCreateCardLinkToSTARTUP() {
        //when
        defaultCardService.changeCardStatusAndCreateCardLink("STARTUP", 1);
        //then
        verify(jdbcCardDao).changeCardStatusAndSetCardLinkById(any(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Change status of card to ISOVER and creating card link(hash)")
    void changeCardStatusAndCreateCardLinkToISOVER() {
        //when
        defaultCardService.changeCardStatusAndCreateCardLink("ISOVER", 1);
        //then
        verify(jdbcCardDao).changeCardStatusAndSetCardLinkById(any(), anyLong(), anyString());
    }

}