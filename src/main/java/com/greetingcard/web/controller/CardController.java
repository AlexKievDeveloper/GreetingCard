package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CardController {

    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(value = "cards")
    public ResponseEntity<Object> getCards(HttpSession session, @RequestParam String type) {
        log.info("getCards");
        User user = (User) session.getAttribute("user");
        long userId = user.getId();
        List<Card> cardList = cardService.getCards(userId, type);
        return ResponseEntity.status(HttpStatus.OK).body(cardList);
    }

    @GetMapping(value = "card/{id}")
    public ResponseEntity<Object> getCard(HttpSession session, @PathVariable long id) throws JsonProcessingException {
        log.info("Get card request");
        User user = (User) session.getAttribute("user");
        Card card = cardService.getCardAndCongratulationByCardId(id, user.getId());
        if (card == null) {
            log.info("User has no access : {}", id);
            String json = objectMapper.writeValueAsString(Map.of("message", "Sorry, you are not a member of this card"));
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(json);
        } else {
            log.info("Successfully writing card to response, id: {}", id);
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(card);
        }
    }

    @PostMapping(value = "card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createCard(@RequestBody Card card, HttpSession session) throws JsonProcessingException {
        log.info("Creating card request");
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        User user = (User) session.getAttribute("user");
        card.setUser(user);
        String json = objectMapper.writeValueAsString(Map.of("id", cardService.createCard(card)));
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(json);
    }

    @PutMapping(value = "card/{id}/status")
    public ResponseEntity<Object> changeStatus(@PathVariable long id) {
        log.info("Received PUT request");
        cardService.changeCardStatus(Status.ISOVER, id);
        log.info("Successfully changed card status for card id: {}", id);
        return ResponseEntity.status(HttpServletResponse.SC_OK).build();
    }

    @DeleteMapping(value = "card/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id, HttpSession session) {
        log.info("Request for DELETE card");
        User user = (User) session.getAttribute("user");
        cardService.deleteCardById(id, user.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping(value = "/leave-card/{id}")
    public ResponseEntity<?> leaveCard(@PathVariable long id, HttpSession session) {
        log.info("Request for leave card with id : {}", id);
        User user = (User) session.getAttribute("user");

        log.info("Request for leave card from user with id : {}", user.getId());
        cardService.leaveCardByCardIdAndUserId(id, user.getId());

        log.info("Successfully leave card with id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
