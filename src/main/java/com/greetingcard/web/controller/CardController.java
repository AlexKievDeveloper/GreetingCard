package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/card", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CardController {
    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getCard(HttpSession session, @PathVariable long id) throws JsonProcessingException {
        log.info("Get card request");
        User user = (User) session.getAttribute("user");
        Card card = cardService.getCardAndCongratulationByCardId(id, user.getId());
        if (card == null) {
            log.info("User has no access : {}", id);
            ObjectMapper mapper = new JsonMapper();
            String json = mapper.writeValueAsString(Map.of("message", "Sorry, you are not a member of this congratulation"));
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(json);
        } else {
            log.info("Successfully writing card to response, id: {}", id);
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(card);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCard(@RequestBody Card card, HttpSession session) throws JsonProcessingException {
        log.info("Creating card request");
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        User user = (User) session.getAttribute("user");
        card.setUser(user);
        ObjectMapper mapper = new JsonMapper();
        String json = mapper.writeValueAsString(Map.of("id", cardService.createCard(card)));
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(json);
    }

    @PutMapping(value = "/{id}/status")
    public ResponseEntity<Object> changeStatus(@PathVariable long id) {
        log.info("Received PUT request");
        cardService.changeCardStatus(Status.ISOVER, id);
        log.info("Successfully changed card status for card id: {}", id);
        return ResponseEntity.status(HttpServletResponse.SC_OK).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id, HttpSession session) {
        log.info("Request for DELETE card");
        User user = (User) session.getAttribute("user");
        cardService.deleteCardById(id, user.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
