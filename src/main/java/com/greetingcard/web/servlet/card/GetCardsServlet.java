package com.greetingcard.web.servlet.card;

import com.alibaba.fastjson.JSON;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetCardsServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getId();

        List<Card> cardsList = cardService.getCards(userId, request.getParameter("cards-type"));
        String json = JSON.toJSONString(cardsList);
        response.getWriter().print(json);
    }
}