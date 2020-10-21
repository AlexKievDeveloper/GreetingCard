package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetCardsServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();

        Map<Card, Role> cards = cardService.getCards(userId, request.getParameter("cards-type"));
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cards", cards);
        PageGenerator.getInstance().process("/after-login", parameters, request, response);
    }
}