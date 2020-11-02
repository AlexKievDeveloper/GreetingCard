package com.greetingcard.web.servlet.card;

import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCardsServletTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private GetCardsServlet getCardsServlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private PrintWriter writer;

    @Test
    @DisplayName("Return all list of user cards")
    void doGetAll() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("cards-type")).thenReturn("All-cards");
        when(response.getWriter()).thenReturn(writer);

        getCardsServlet.doGet(request, response);

        verify(cardService).getCards(1, "All-cards");
        verify(writer).print(anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Return all list of user cards where user admin")
    void doGetAdmin() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("cards-type")).thenReturn("My-cards");
        when(response.getWriter()).thenReturn(writer);

        getCardsServlet.doGet(request, response);

        verify(cardService).getCards(1, "My-cards");
        verify(writer).print(anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Return all list of user cards")
    void doGet() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("cards-type")).thenReturn("Another`s-cards");
        when(response.getWriter()).thenReturn(writer);

        getCardsServlet.doGet(request, response);

        verify(cardService).getCards(1, "Another`s-cards");
        verify(writer).print(anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Exception while return all list of user cards")
    void doGetException() throws IOException {
        User user = User.builder().id(1).build();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("cards-type")).thenReturn("Another`s-cards");
        when(response.getWriter()).thenReturn(writer);
        doThrow(RuntimeException.class).when(cardService).getCards(1, "Another`s-cards");

        getCardsServlet.doGet(request, response);

        verify(cardService).getCards(1, "Another`s-cards");
        verify(writer).print(anyString());
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}