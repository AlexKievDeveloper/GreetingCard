package com.greetingcard.web.filter;

import com.greetingcard.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationFilterTest {
    private AuthorizationFilter authorizationFilter;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpSession session;
    @Mock
    private User user;

    AuthorizationFilterTest() {
        authorizationFilter = new AuthorizationFilter();
    }

    @Test
    @DisplayName("Verify session for user and redirect request and response to the next filter in chain")
    void doFilterIfUserNotNull() throws IOException, ServletException {
        //prepare
        when(servletRequest.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(servletRequest.getRequestURI()).thenReturn("/edit_card");
        //when
        authorizationFilter.doFilter(servletRequest, servletResponse, filterChain);
        //then
        verify(servletRequest).getSession();
        verify(session).getAttribute("user");
        verify(filterChain).doFilter(servletRequest, servletResponse);
    }

    @Test
    @DisplayName("Verify session for user and redirect request and response to login page")
    void doFilterIfUserEqualsNull() throws IOException, ServletException {
        //prepare
        when(servletRequest.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(servletRequest.getRequestURI()).thenReturn("/edit_card");
        //when
        authorizationFilter.doFilter(servletRequest, servletResponse, filterChain);
        //then
        verify(servletRequest).getSession();
        verify(session).getAttribute("user");
        verify(servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}