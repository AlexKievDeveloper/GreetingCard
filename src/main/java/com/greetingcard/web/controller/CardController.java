package com.greetingcard.web.controller;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CardController {
    private CardService cardService;

    //TODO выводить нормальный месседж для незалогиненого юзера с кривой ссылкой
    @GetMapping("cards")
    public ResponseEntity<Object> getCards(HttpSession session, @RequestParam("type") CardsType type) {//TODO Tell to front send us caps-lock types
        log.info("getCards");
        User user = (User) session.getAttribute("user");
        long userId = user.getId();
        List<Card> cardList = cardService.getCards(userId, type);
        return ResponseEntity.status(HttpStatus.OK).body(cardList);
    }

    @GetMapping("card/{id}")
    public ResponseEntity<Object> getCard(HttpSession session, @PathVariable long id) {
        log.info("Get card request");
        User user = (User) session.getAttribute("user");
        Card card = cardService.getCardAndCongratulationByCardId(id, user.getId());
        log.info("Successfully writing card to response, id: {}", id);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(card);
    }

    @PostMapping(value = "card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createCard(@RequestBody Card card, HttpSession session) {
        log.info("Creating card request");
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        User user = (User) session.getAttribute("user");
        card.setUser(user);

        log.info("Сard successefully created");
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(Map.of("id", cardService.createCard(card)));
    }

    @PutMapping("card/{id}/status")
    public void changeStatus(@PathVariable long id) {
        log.info("Received PUT request");
        cardService.changeCardStatus(Status.ISOVER, id);
        log.info("Successfully changed card status for card id: {}", id);
    }

    @DeleteMapping("card/{id}")
    public void delete(@PathVariable long id, HttpSession session) {
        log.info("Request for DELETE card");
        User user = (User) session.getAttribute("user");
        cardService.deleteCardById(id, user.getId());
    }

    @PutMapping("card/{id}/name")
    public void changeName(@RequestBody Card card, @PathVariable long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        card.setId(id);
        card.setUser(user);
        cardService.changeCardName(card);
    }
}
