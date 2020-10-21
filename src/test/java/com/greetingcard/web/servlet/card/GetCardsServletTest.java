package com.greetingcard.web.servlet.card;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private User user;
    @Mock
    private Map<Card, Role> cards;
    @Mock
    private PrintWriter writer;
    @Mock
    private ServletContext context;

    @Test
    @DisplayName("Returns page with all cards  to client")
    void doGet() throws IOException {
        //prepare
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1);
        when(request.getParameter("cards-type")).thenReturn("All-cards");
        when(cardService.getCards(1, "All-cards")).thenReturn(cards);
        when(request.getServletContext()).thenReturn(context);
        when(response.getWriter()).thenReturn(writer);
        //when
        getCardsServlet.doGet(request, response);
        //then
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(request).getParameter("cards-type");
        verify(cardService).getCards(1, "All-cards");
        verify(request).getServletContext();
        verify(response).getWriter();
    }
}