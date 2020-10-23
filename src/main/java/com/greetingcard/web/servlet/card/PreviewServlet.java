package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.service.CardService;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreviewServlet extends HttpServlet {
    CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int cardId = Integer.parseInt(request.getParameter("card_id"));
        Role role = Role.valueOf(request.getParameter("role"));

        Card card = cardService.getCardAndCongratulationByCardId(cardId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("card", card);
        parameters.put("role",role);
        PageGenerator.getInstance().process("preview", parameters, request, response);
    }
}
