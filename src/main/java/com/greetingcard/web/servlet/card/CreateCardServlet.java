package com.greetingcard.web.servlet.card;

import com.greetingcard.ServiceLocator;
import com.greetingcard.service.CardService;

import javax.servlet.http.HttpServlet;

public class CreateCardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

}
