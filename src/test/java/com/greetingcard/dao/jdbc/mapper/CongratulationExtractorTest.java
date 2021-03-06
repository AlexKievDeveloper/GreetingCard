package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CongratulationExtractorTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class Congratulation from result set")
    void extractData() throws SQLException {
        //prepare
        CongratulationExtractor congratulationExtractor = new CongratulationExtractor();
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("congratulation_id")).thenReturn(1);
        when(mockResultSet.getString("message")).thenReturn("from Roma");
        when(mockResultSet.getLong("card_id")).thenReturn(1L);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("status_id")).thenReturn(1);
        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("you_tube_1");
        when(mockResultSet.getInt("type_id")).thenReturn(1);

        //when
        Congratulation actualCongratulation = congratulationExtractor.extractData(mockResultSet);

        //then
        assertNotNull(actualCongratulation);
        verify(mockResultSet, times(2)).next();
        verify(mockResultSet, times(2)).getInt("congratulation_id");
        verify(mockResultSet).getString("message");
        verify(mockResultSet).getLong("card_id");
        verify(mockResultSet).getInt("user_id");
        verify(mockResultSet).getInt("status_id");
        verify(mockResultSet, times(2)).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("type_id");
        assertEquals(1, actualCongratulation.getId());
        assertEquals("from Roma", actualCongratulation.getMessage());
        assertEquals(1, actualCongratulation.getCardId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());
    }

    @Test
    @DisplayName("Empty result set")
    void extractDataEmptyResultSet() {
        //prepare
        CongratulationExtractor congratulationExtractor = new CongratulationExtractor();
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> congratulationExtractor.extractData(mockResultSet));
        assertEquals("Empty result set for requested congratulation", e.getMessage());
    }
}
