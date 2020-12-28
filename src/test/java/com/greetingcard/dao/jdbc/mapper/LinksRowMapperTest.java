package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinksRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    void extractData() throws SQLException {
        //prepare
        LinksRowMapper linksRowMapper = new LinksRowMapper();
        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("/img/picture.png");
        when(mockResultSet.getInt("congratulation_id")).thenReturn(1);
        when(mockResultSet.getInt("type_id")).thenReturn(2);

        //when
        Link actualLinks = linksRowMapper.mapRow(mockResultSet, 0);

        //then
        assertNotNull(actualLinks);
        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("congratulation_id");
        verify(mockResultSet).getInt("type_id");
    }
}