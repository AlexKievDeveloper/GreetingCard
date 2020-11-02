package com.greetingcard.web.servlet.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class CreateCardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> nameOfCard = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
        });
        String name = nameOfCard.get("create-card");

        Card card = Card.builder().user(user).name(name).build();
        try {
            cardService.createCard(card);
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
