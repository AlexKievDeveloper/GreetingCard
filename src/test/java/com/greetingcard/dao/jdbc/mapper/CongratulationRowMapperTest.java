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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CongratulationRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class Congratulation from result set")
    void mapRow() throws SQLException {
        //prepare
        CongratulationRowMapper congratulationRowMapper = new CongratulationRowMapper();
        when(mockResultSet.getInt("congratulation_id")).thenReturn(1);
        when(mockResultSet.getString("message")).thenReturn("from Roma");
        when(mockResultSet.getInt("card_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("status_id")).thenReturn(1);
        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("you_tube_1");
        when(mockResultSet.getInt("type_id")).thenReturn(1);

        //when
        Congratulation actualCongratulation = congratulationRowMapper.mapRow(mockResultSet);

        //then
        assertEquals(1, actualCongratulation.getId());
        assertEquals("from Roma", actualCongratulation.getMessage());
        assertEquals(1, actualCongratulation.getCard().getId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());
    }
}
