package com.greetingcard.web.servlet.card;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
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

import static org.mockito.Mockito.*;

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

//    @Test
//    @DisplayName("Create new card")
//    void doPost() throws IOException {
//        User user = User.builder().build();
//        Card card = Card.builder().user(user).name("test").status(Status.STARTUP).build();
//        when(request.getParameter("create-card")).thenReturn("test");
//        when(request.getSession()).thenReturn(session);
//        when(session.getAttribute("user")).thenReturn(user);
//
//        servlet.doPost(request,response);
//
//        verify(request).getParameter("create-card");
//        verify(cardService).createCard(card);
//        verify(response).sendRedirect("/editCard");
//    }
}