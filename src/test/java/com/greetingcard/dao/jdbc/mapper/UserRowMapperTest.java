package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRowMapperTest {
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class User from result set")
    void mapRowTest() throws SQLException {
        //prepare
        UserRowMapper userRowMapper = new UserRowMapper();
        when(mockResultSet.getInt("user_id")).thenReturn(2);
        when(mockResultSet.getString("firstName")).thenReturn("user");
        when(mockResultSet.getString("lastName")).thenReturn("user");
        when(mockResultSet.getString("login")).thenReturn("user");
        when(mockResultSet.getString("email")).thenReturn("@user");
        when(mockResultSet.getString("password")).
                thenReturn("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df");
        when(mockResultSet.getString("salt")).thenReturn("salt");
        when(mockResultSet.getInt("language_id")).thenReturn(2);
        //when
        User actualUser = userRowMapper.mapRow(mockResultSet, 0);
        //then
        assertNotNull(actualUser);
        assertEquals(2, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());
        assertEquals("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df",
                actualUser.getPassword());
        assertEquals("salt", mockResultSet.getString("salt"));
        assertEquals(Language.ENGLISH, actualUser.getLanguage());
    }
}