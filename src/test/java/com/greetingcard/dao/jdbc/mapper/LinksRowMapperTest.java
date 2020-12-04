package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinksRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    void extractData() throws SQLException {
        //prepare
        LinksRowMapper linksRowMapper = new LinksRowMapper();
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("/img/picture.png");
        when(mockResultSet.getInt("congratulation_id")).thenReturn(1);
        when(mockResultSet.getInt("type_id")).thenReturn(2);

        //when
        List<Link> actualLinks = linksRowMapper.extractData(mockResultSet);

        //then
        assertNotNull(actualLinks);
        verify(mockResultSet, times(2)).next();
        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("congratulation_id");
        verify(mockResultSet).getInt("type_id");
        assertEquals(1, actualLinks.get(0).getId());
        assertEquals("/img/picture.png", actualLinks.get(0).getLink());
        assertEquals(1, actualLinks.get(0).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualLinks.get(0).getType());
    }
}