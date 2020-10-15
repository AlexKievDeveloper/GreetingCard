package com.greetingcard.security;

import com.greetingcard.dao.jdbc.JdbcUserDao;
import com.greetingcard.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSecurityServiceTest {
    private Session session;
    private ArrayList<Session> sessionList;
    @Mock
    private JdbcUserDao jdbcUserDao;
    @InjectMocks
    private DefaultSecurityService securityService;
    @Mock
    private User user;

    DefaultSecurityServiceTest() {

        session = Session.builder()
                .token("sessionToken")
                .expireDate(LocalDateTime.now().plusSeconds(60))
                .build();
        sessionList = new ArrayList<>();
        sessionList.add(session);
    }

    @Test
    @DisplayName("Login user and return session")
    void loginTest() {
        //prepare
        when(jdbcUserDao.findUserByLogin("user")).thenReturn(user);
        when(user.getSalt()).thenReturn("salt");
        when(user.getPassword()).thenReturn("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df");
        //when
        securityService.login("user", "8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df");
        //then
        verify(jdbcUserDao).findUserByLogin("user");
        verify(user).getSalt();
        verify(user).getPassword();
    }

    @Test
    @DisplayName("Removing session from session list")
    void logoutTest() {
        //prepare
        securityService.setSessionList(sessionList);
        assertEquals(session, securityService.getSession("sessionToken"));
        //when
        securityService.logout("sessionToken");
        //then
        assertNull(securityService.getSession("sessionToken"));
    }

    @Test
    @DisplayName("Returns session from sessions list")
    void getSessionTest() {
        //prepare
        securityService.setSessionList(sessionList);
        //when
        Session actual = securityService.getSession("sessionToken");
        //then
        assertEquals(session, actual);
    }

    @Test
    @DisplayName("Returns hashed password")
    void getHashPasswordTest() {
        //prepare
        String salt = "salt";
        String testPassword = "user";
        String expectedPassword = "8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df";
        //when
        String actualPassword = securityService.getHashPassword(salt, testPassword);
        //then
        assertEquals(expectedPassword, actualPassword);
    }
}