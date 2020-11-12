package com.greetingcard.web.servlet.card;

import com.alibaba.fastjson.JSON;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class GetCardsServlet extends HttpServlet {
    @Autowired
    private CardService cardService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Received GET request");
        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getId();
        log.info("Received GET request for cards with type: {}, from user: {}", request.getParameter("type"), user.getLogin());

        try {
            List<Card> cardsList = cardService.getCards(userId, request.getParameter("type"));
            String json = JSON.toJSONString(cardsList == null ? Collections.EMPTY_LIST : cardsList);
            response.getWriter().print(json);
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Successfully writing cards to response with type: {}, for user: {}", request.getParameter("type"), user.getLogin());
        } catch (RuntimeException e) {
            response.getWriter().println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while getting cards with type: {}, from user: {}", request.getParameter("type"), user.getLogin());
        }
    }
}