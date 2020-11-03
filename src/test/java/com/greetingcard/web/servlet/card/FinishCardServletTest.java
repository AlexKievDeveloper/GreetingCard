package com.greetingcard.web.servlet.card;

import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
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
class FinishCardServletTest {
    @Mock
    private ServletInputStream inputStream;
    @Mock
    private CardService cardService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @InjectMocks
    private FinishCardServlet finishCardServlet;

    @Test
    @DisplayName("Changing card status")
    void doPut() throws IOException {
        //prepare
        byte[] bytes = "{\"id\":\"1\"}".getBytes();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        //when
        finishCardServlet.doPut(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(cardService).changeCardStatus(Status.ISOVER, 1);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Changing card status")
    void doPutException() throws IOException {
        //prepare
        byte[] bytes = "{\"id\":\"1\"}".getBytes();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(RuntimeException.class).when(cardService).changeCardStatus(Status.ISOVER, 1);
        //when
        finishCardServlet.doPut(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(cardService).changeCardStatus(Status.ISOVER, 1);
        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}