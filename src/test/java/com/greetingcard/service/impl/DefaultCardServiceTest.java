package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CongratulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        defaultCardService.getCards(1, CardsType.All);
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
        defaultCardService.getCardAndCongratulationByCardId(1, 1);
        //then
        verify(jdbcCardDao).getCardAndCongratulationByCardId(1, 1);
    }

    @Test
    @DisplayName("Return List with all congratulations")
    void changeCardStatus() {
        //when
        defaultCardService.changeCardStatus(Status.STARTUP, 1);
        //then
        verify(jdbcCardDao).changeCardStatusById(Status.STARTUP, 1);
        verify(service).changeCongratulationStatusByCardId(Status.STARTUP, 1);
    }
}