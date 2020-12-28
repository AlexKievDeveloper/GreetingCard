package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Congratulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CongratulationsExtractorTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns all congratulations from result set")
    void extractData() throws SQLException {
        //prepare
        CongratulationsExtractor rowMapper = new CongratulationsExtractor();

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("card_id")).thenReturn(1L);
        when(mockResultSet.getLong("user_id")).thenReturn(1L);
        when(mockResultSet.getLong("congratulation_id")).thenReturn(1L);
        when(mockResultSet.getString("message")).thenReturn("from Roma");
        when(mockResultSet.getInt("status_id")).thenReturn(1);
        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("/link");
        when(mockResultSet.getInt("type_id")).thenReturn(1);

        //when
        List<Congratulation> congratulationList = rowMapper.extractData(mockResultSet);

        //then
        assertNotNull(congratulationList);
        verify(mockResultSet).getLong("card_id");
        verify(mockResultSet).getLong("congratulation_id");
        verify(mockResultSet).getString("message");
        verify(mockResultSet).getLong("user_id");
        verify(mockResultSet).getInt("status_id");
        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("type_id");
        assertEquals(1L, congratulationList.get(0).getCardId());
        assertEquals(1L, congratulationList.get(0).getUser().getId());
        assertEquals(1L, congratulationList.get(0).getId());
        assertEquals("from Roma", congratulationList.get(0).getMessage());
        assertEquals(1, congratulationList.get(0).getStatus().getStatusNumber());
        assertEquals(1, congratulationList.get(0).getLinkList().get(0).getId());
        assertEquals("/link", congratulationList.get(0).getLinkList().get(0).getLink());
        assertEquals(1, congratulationList.get(0).getLinkList().get(0).getType().getTypeNumber());
    }
}