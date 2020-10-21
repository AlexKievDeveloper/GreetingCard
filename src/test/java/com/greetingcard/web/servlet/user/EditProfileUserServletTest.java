package com.greetingcard.web.servlet.user;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.servlet.user.EditProfileUserServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditProfileUserServletTest {
    @Mock
    private SecurityService service;
    @InjectMocks
    private EditProfileUserServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private ServletContext servletContext;
    @Mock
    private HttpSession session;

    @Test
    void doGet() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getServletContext()).thenReturn(servletContext);

        servlet.doGet(request, response);

        verify(response, times(1)).setContentType("text/html;charset=utf-8");
    }

    @Test
    void doPost() throws IOException {
        User user = User.builder().build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("firstName")).thenReturn("test");
        when(request.getParameter("lastName")).thenReturn("test");
        when(request.getParameter("login")).thenReturn("test");

        //when
        servlet.doPost(request, response);
        //then
        verify(request, times(1)).getParameter("firstName");
        verify(request, times(1)).getParameter("lastName");
        verify(request, times(1)).getParameter("login");
        verify(service, times(1)).update(user);
        verify(session,times(1)).setAttribute("user",user);
    }
}