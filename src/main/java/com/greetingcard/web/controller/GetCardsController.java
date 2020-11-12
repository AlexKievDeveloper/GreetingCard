package com.greetingcard.web.controller;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class GetCardsController {
    private CardService cardService;

    @Autowired
    public GetCardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(value = "/cards", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Card> getCards(HttpSession session, HttpServletResponse response, @RequestParam String type) {
        log.info("getCards");
        User user = (User) session.getAttribute("user");
        long userId = user.getId();

        response.setContentType("application/json");
        return cardService.getCards(userId, type);
    }
}
