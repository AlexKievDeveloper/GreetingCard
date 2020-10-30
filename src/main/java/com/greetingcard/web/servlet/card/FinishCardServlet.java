package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FinishCardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        long cardId = Long.parseLong("1");
        cardService.changeCardStatus(Status.ISOVER,cardId);
    }

}
