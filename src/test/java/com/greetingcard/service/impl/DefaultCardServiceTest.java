package com.greetingcard.service.impl;

import com.greetingcard.dao.CardDao;
import com.greetingcard.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultCardServiceTest {
    @Mock
    private CardDao jdbcCardDao;
    @InjectMocks
    private DefaultCardService defaultCardService;

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsAllCards() {
        //when
        defaultCardService.getCards(1, "all");
        //then
        verify(jdbcCardDao).getAllCardsByUserId(1);
    }

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsMyCards() {
        //when
        defaultCardService.getCards(1, "my");
        //then
        verify(jdbcCardDao).getCardsByUserIdAndRoleId(1, 1);
    }

    @Test
    @DisplayName("Returns list with cards")
    void getCardsWhenParameterIsAnotherCards() {
        //when
        defaultCardService.getCards(1, "other");
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
    }
}