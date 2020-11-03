package com.greetingcard.web.servlet.card;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServletTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CardServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private ServletInputStream inputStream;
    @Mock
    private PrintWriter writer;

    @Test
    @DisplayName("Create new card")
    void doPost() throws IOException {
        User user = User.builder().id(1).login("user").build();
        byte[] bytes = "{\"name\":\"new_Card\"}".getBytes();
        Card card = Card.builder().user(user).name("new_Card").build();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(cardService.createCard(any())).thenReturn(1L);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(cardService).createCard(card);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();
        verify(writer).print(anyString());
    }

    @Test
    @DisplayName("Throws Exception while creating card")
    void doPostException() throws IOException {
        User user = User.builder().id(1).login("user").build();
        byte[] bytes = "{\"name\":\"new_Card\"}".getBytes();
        Card card = Card.builder().user(user).name("new_Card").build();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);
        doThrow(RuntimeException.class).when(cardService).createCard(card);

        servlet.doPost(request, response);

        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(cardService).createCard(card);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Get card by id")
    void doGet() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getPathInfo()).thenReturn("/card/22");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getPathInfo();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(cardService).getCardAndCongratulationByCardId(22, user.getId());
        verify(response).getWriter();
        verify(writer).print(anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Throws Exception while getting card by id")
    void doGetException() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getPathInfo()).thenReturn("/card/22");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);
        doThrow(RuntimeException.class).when(cardService).getCardAndCongratulationByCardId(22, user.getId());

        servlet.doGet(request, response);

        verify(request).getPathInfo();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(cardService).getCardAndCongratulationByCardId(22, user.getId());
        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

