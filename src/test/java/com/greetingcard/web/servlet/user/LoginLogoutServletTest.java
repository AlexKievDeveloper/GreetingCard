package com.greetingcard.web.servlet.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginLogoutServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private BufferedReader bufferedReader;
    @Mock
    private Stream<String> stringStream;

    @Test
    void doDelete() {
    }
/*
    @Test
    @DisplayName("I don`t know how it works yet")
    void doPost() throws IOException {
        //prepare
        LoginLogoutServlet loginLogoutServlet = new LoginLogoutServlet();
        String json = "{\"login\":\"user\",\"password\":\"password\"}";
        when(request.getReader()).thenReturn(bufferedReader);
        when(bufferedReader.lines()).thenReturn(stringStream);
        when(stringStream.toString()).thenReturn(json);
        //when
        loginLogoutServlet.doPost(request, response);
        //then
        verify(request).getReader();
        verify(bufferedReader).lines();
        verify(stringStream).toString();
    }*/
}