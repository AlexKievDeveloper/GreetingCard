package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.JdbcCardDao;
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
    private JdbcCardDao jdbcCardDao;
    @InjectMocks
    private DefaultCardService defaultCardService;

    @Test
    @DisplayName("Returns map with cards")
    void getCardsWhenParameterIsAllCards() {
        //when
        defaultCardService.getCards(1, "All-cards");
        //then
        verify(jdbcCardDao).getAllCardsByUserId(1);
    }

    @Test
    @DisplayName("Returns map with cards")
    void getCardsWhenParameterIsMyCards() {
        //when
        defaultCardService.getCards(1, "My-cards");
        //then
        verify(jdbcCardDao).getCardsByUserIdAndRoleId(1, 1);
    }

    @Test
    @DisplayName("Returns map with cards")
    void getCardsWhenParameterIsAnotherCards() {
        //when
        defaultCardService.getCards(1, "Another`s-cards");
        //then
        verify(jdbcCardDao).getCardsByUserIdAndRoleId(1, 2);
    }

    @Test
    @DisplayName("Return card with all congatulations")
    void getCardAndCongratulationByCardId() {
        //when
        defaultCardService.getCardAndCongratulationByCardId(1, 1);
        //then
        verify(jdbcCardDao).getCardAndCongratulationByCardId(1, 1);
    }
}