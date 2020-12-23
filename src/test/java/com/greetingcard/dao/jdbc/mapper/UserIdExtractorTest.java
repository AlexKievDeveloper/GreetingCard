package com.greetingcard.dao.jdbc.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserIdExtractorTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns user id")
    void extractData() throws SQLException {
        //prepare
        UserIdExtractor userIdExtractor = new UserIdExtractor();
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("user_id")).thenReturn(1L);

        //when
        long actualUserId = userIdExtractor.extractData(mockResultSet);

        //then
        assertEquals(1L, actualUserId);
    }

    @Test
    @DisplayName("Throws IllegalArgumentException")
    void extractDataEmptyResultSet() throws SQLException {
        //prepare
        UserIdExtractor userIdExtractor = new UserIdExtractor();
        when(mockResultSet.next()).thenReturn(false);

        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userIdExtractor.extractData(mockResultSet));
        assertEquals("No user found for requested hash", e.getMessage());
    }
}