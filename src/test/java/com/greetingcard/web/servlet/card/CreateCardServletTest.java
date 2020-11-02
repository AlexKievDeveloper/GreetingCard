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

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCardServletTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CreateCardServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private ServletInputStream inputStream;

    @Test
    @DisplayName("Create new cart")
    void doPost() throws ServletException, IOException {
        User user = User.builder().id(1).build();
        byte[] bytes = "{\"create-card\":\"new_Card\"}".getBytes();
        Card card = Card.builder().user(user).name("new_Card").build();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);

        servlet.doPost(request, response);

        verify(cardService).createCard(card);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }
}