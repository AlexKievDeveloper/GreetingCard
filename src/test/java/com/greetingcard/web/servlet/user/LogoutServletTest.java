package com.greetingcard.web.servlet.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    @Test
    @DisplayName("Removes session with attribute user")
    void doGet() throws IOException {
        //prepare
        LogoutServlet logoutServlet = new LogoutServlet();
        when(request.getSession()).thenReturn(session);
        //when
        logoutServlet.doGet(request, response);
        //then
        verify(request).getSession();
        verify(session).invalidate();
        verify(response).sendRedirect("/login");
    }
}