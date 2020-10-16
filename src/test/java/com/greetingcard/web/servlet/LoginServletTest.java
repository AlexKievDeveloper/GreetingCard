package com.greetingcard.web.servlet;

import com.greetingcard.security.SecurityService;
import com.greetingcard.security.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class LoginServletTest {
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private LoginServlet loginServlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private Session session;

    @Test
    @DisplayName("Processes the request and sends a page with login form")
    void doGetTest() throws IOException {
        //prepare
        when(response.getWriter()).thenReturn(printWriter);
        //when
        loginServlet.doGet(request, response);
        //then
        verify(response).setContentType("text/html;charset=utf-8");
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Redirect to home page if session != null")
    void doPostIfSessionExistTest() throws IOException {
        //prepare
        when(request.getParameter("login")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("user");
        when(securityService.login("user", "user")).thenReturn(session);
        //when
        loginServlet.doPost(request, response);
        //then
        verify(request).getParameter("login");
        verify(request).getParameter("password");
        verify(response).addCookie(any());
        verify(response).sendRedirect("/");
    }

    @Test
    @DisplayName("Redirect to login page if session == null")
    void doPostIfSessionNotExistTest() throws IOException {
        //prepare
        when(request.getParameter("login")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("user");
        when(securityService.login("user", "user")).thenReturn(null);
        //when
        loginServlet.doPost(request, response);
        //then
        verify(request).getParameter("login");
        verify(request).getParameter("password");
        verify(securityService).login("user", "user");
        verify(response).sendRedirect("/login?message=Access denied. Please login and try again.");
    }
}