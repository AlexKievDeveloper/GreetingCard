package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateCardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cardName = request.getParameter("create_card");
        User user = (User) request.getSession().getAttribute("user");
        Card card = Card.builder()
                .name(cardName)
                .status(Status.STARTUP)
                .build();
        cardService.createCard(card, user);
        response.sendRedirect("/editCard");
    }
}
