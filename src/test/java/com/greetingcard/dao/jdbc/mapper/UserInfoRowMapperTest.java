package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoRowMapperTest {

    @Mock
    private ResultSet mockResultSet;

    @Test
    void mapRow() throws SQLException {
        UserInfoRowMapper userInfoRowMapper = new UserInfoRowMapper();
        when(mockResultSet.getInt("user_id")).thenReturn(2);
        when(mockResultSet.getString("firstName")).thenReturn("user");
        when(mockResultSet.getString("lastName")).thenReturn("userLastName");
        when(mockResultSet.getString("login")).thenReturn("userLogin");
        when(mockResultSet.getString("email")).thenReturn("@userEmail");
        when(mockResultSet.getString("pathToPhoto")).thenReturn("userPathToPhoto");

        UserInfo userInfoActual = userInfoRowMapper.mapRow(mockResultSet, 0);

        assertNotNull(userInfoActual);
        assertEquals(2, userInfoActual.getId());
        assertEquals("user", userInfoActual.getFirstName());
        assertEquals("userLastName", userInfoActual.getLastName());
        assertEquals("userLogin", userInfoActual.getLogin());
        assertEquals("@userEmail", userInfoActual.getEmail());
        assertEquals("userPathToPhoto", userInfoActual.getPathToPhoto());
    }
}