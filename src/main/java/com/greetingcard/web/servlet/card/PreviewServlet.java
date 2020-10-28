package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PreviewServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        long cardId = Long.parseLong(request.getParameter("card_id"));
        User user = (User) request.getSession().getAttribute("user");

        Card card = cardService.getCardAndCongratulationByCardId(cardId, user.getId());
    }
}
