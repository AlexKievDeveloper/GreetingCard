package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class Card from result set")
    void mapRow() throws SQLException {
        //prepare
        CardRowMapper cardRowMapper = new CardRowMapper();
        when(mockResultSet.getLong("user_id")).thenReturn(1L);
        when(mockResultSet.getString("firstName")).thenReturn("firstName");
        when(mockResultSet.getString("lastName")).thenReturn("lastName");
        when(mockResultSet.getString("login")).thenReturn("login");
        when(mockResultSet.getString("email")).thenReturn("email");
        when(mockResultSet.getLong("card_id")).thenReturn(1L);
        when(mockResultSet.getString("name")).thenReturn("Card");
        when(mockResultSet.getString("background_image")).thenReturn("/link");
        when(mockResultSet.getString("card_link")).thenReturn("/link");
        when(mockResultSet.getInt("status_id")).thenReturn(1);
        //when
        Card actualCard = cardRowMapper.mapRow(mockResultSet);
        User actualUser = actualCard.getUser();
        //then
        verify(mockResultSet).getLong("user_id");
        verify(mockResultSet).getString("firstName");
        verify(mockResultSet).getString("lastName");
        verify(mockResultSet).getString("login");
        verify(mockResultSet).getString("email");
        verify(mockResultSet).getLong("card_id");
        verify(mockResultSet).getString("name");
        verify(mockResultSet).getString("background_image");
        verify(mockResultSet).getString("card_link");
        verify(mockResultSet).getInt("status_id");

        assertEquals(1, actualUser.getId());
        assertEquals("firstName", actualUser.getFirstName());
        assertEquals("lastName", actualUser.getLastName());
        assertEquals("login", actualUser.getLogin());
        assertEquals("email", actualUser.getEmail());
        assertEquals(1, actualCard.getId());
        assertEquals("Card", actualCard.getName());
        assertEquals("/link", actualCard.getBackgroundImage());
        assertEquals("/link", actualCard.getCardLink());
        assertEquals(Status.STARTUP, actualCard.getStatus());
    }
}