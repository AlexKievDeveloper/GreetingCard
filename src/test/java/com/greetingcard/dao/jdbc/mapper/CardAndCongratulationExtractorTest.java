package com.greetingcard.dao.jdbc.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardAndCongratulationExtractorTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class Card with all congratulations from result set")
    void extractData() throws SQLException {
        //prepare
        CardAndCongratulationExtractor rowMapper = new CardAndCongratulationExtractor();

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("card_user")).thenReturn(1L);
        when(mockResultSet.getLong("card_id")).thenReturn(1L);
        when(mockResultSet.getString("name")).thenReturn("Card");
        when(mockResultSet.getString("background_image")).thenReturn("/link");
        when(mockResultSet.getString("card_link")).thenReturn("/link");
        when(mockResultSet.getInt("status_id")).thenReturn(1);

        when(mockResultSet.getLong("user_id")).thenReturn(1L);
        when(mockResultSet.getString("firstName")).thenReturn("user");
        when(mockResultSet.getString("lastName")).thenReturn("user");
        when(mockResultSet.getString("login")).thenReturn("user");

        when(mockResultSet.getLong("congratulation_id")).thenReturn(1L);
        when(mockResultSet.getString("message")).thenReturn("from Roma");
        when(mockResultSet.getInt("con_status")).thenReturn(1);

        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("/link");
        when(mockResultSet.getInt("type_id")).thenReturn(1);

        //when
        rowMapper.extractData(mockResultSet);

        //then
        verify(mockResultSet).getLong("card_user");
        verify(mockResultSet).getLong("card_id");
        verify(mockResultSet).getString("name");
        verify(mockResultSet).getString("background_image");
        verify(mockResultSet).getString("card_link");
        verify(mockResultSet).getInt("status_id");

        verify(mockResultSet).getLong("user_id");
        verify(mockResultSet).getString("firstName");
        verify(mockResultSet).getString("lastName");
        verify(mockResultSet).getString("login");

        verify(mockResultSet).getLong("congratulation_id");
        verify(mockResultSet).getString("message");
        verify(mockResultSet).getInt("con_status");

        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("type_id");
    }
}