package com.greetingcard.web.servlet.user;

import com.greetingcard.security.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServletTest {
    @Mock
    private ServletInputStream inputStream;
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

    @Test
    @DisplayName("Registration new user")
    void doPost() throws IOException {
        //prepare
        String userJson = "{\n" +
                "  \"firstName\" : \"user\",\n" +
                "  \"lastName\" : \"user\",\n" +
                "  \"email\" : \"user@\",\n" +
                "  \"login\" : \"user\",\n" +
                "  \"password\" : \"user\" \n" +
                "}";

        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(userJson.getBytes());
        //when
        servlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(service).save(any());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Exception when registration new user")
    void doPostException() throws IOException {
        //prepare
        String userJson = "{\n" +
                "  \"firstName\" : \"user\",\n" +
                "  \"lastName\" : \"user\",\n" +
                "  \"email\" : \"user@\",\n" +
                "  \"login\" : \"user\",\n" +
                "  \"password\" : \"user\" \n" +
                "}";

        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(userJson.getBytes());
        doThrow(RuntimeException.class).when(service).save(any());
        when(response.getWriter()).thenReturn(printWriter);

        //when
        servlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(service).save(any());
        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}