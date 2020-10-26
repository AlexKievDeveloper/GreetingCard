package com.greetingcard.web.servlet.card;

import com.greetingcard.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreviewServletTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private PreviewServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("Return card to client")
    void doGet() throws ServletException, IOException {
        when(request.getParameter("card_id")).thenReturn("1");
        when(request.getParameter("role")).thenReturn("ADMIN");

        servlet.doGet(request,response);

        verify(request).getParameter("card_id");
        verify(request).getParameter("role");
        verify(cardService).getCardAndCongratulationByCardId(1);
    }
}