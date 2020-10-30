package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Congratulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CongratulationsRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns all congratulations from result set")
    void mapRow() throws SQLException {
        //prepare
        CongratulationsRowMapper rowMapper = new CongratulationsRowMapper();

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
        List<Congratulation> congratulationList = rowMapper.mapRow(mockResultSet);

        //then
        verify(mockResultSet).getLong("card_id");
        verify(mockResultSet).getLong("congratulation_id");
        verify(mockResultSet).getString("message");
        verify(mockResultSet).getLong("user_id");
        verify(mockResultSet).getInt("status_id");
        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("type_id");
    }
}