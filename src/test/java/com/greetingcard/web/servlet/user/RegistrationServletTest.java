package com.greetingcard.web.servlet.user;

import com.greetingcard.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {
    @Mock
    private SecurityService service;
    @InjectMocks
    private RegistrationServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private ServletContext servletContext;

    @Test
    void doGet() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getServletContext()).thenReturn(servletContext);

        servlet.doGet(request, response);

        verify(response, times(1)).setContentType("text/html;charset=utf-8");
    }

    @Test
    void doPost() throws IOException {
        when(request.getParameter("firstName")).thenReturn("test");
        when(request.getParameter("lastName")).thenReturn("test");
        when(request.getParameter("email")).thenReturn("test");
        when(request.getParameter("login")).thenReturn("test");
        when(request.getParameter("password")).thenReturn("test");

        //when
        servlet.doPost(request, response);
        //then
        verify(request, times(1)).getParameter("firstName");
        verify(request, times(1)).getParameter("lastName");
        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("login");
        verify(request, times(1)).getParameter("password");
        verify(service, times(1)).save(any());
    }
}