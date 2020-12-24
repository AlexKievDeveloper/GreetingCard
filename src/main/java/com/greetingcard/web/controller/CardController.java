package com.greetingcard.web.controller;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.CardsType;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CardController {
    private final CardService cardService;
    @Value("${webapp.url}")
    private String siteUrl;

    @GetMapping("cards")
    public ResponseEntity<Object> getCards(HttpSession session, @RequestParam CardsType type) {
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
        Card card = cardService.getCardAndCongratulationByCardIdAndUserId(id, user.getId());

        if (card.getStatus().equals(Status.ISOVER)) {
            card.setCardLink(siteUrl + "card/" + card.getId() + "/card_link/" + card.getCardLink());
        }
        log.info("Successfully writing card to response, id: {}", id);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(card);
    }

    @GetMapping("card/{id}/card_link/{hash}")
    public ResponseEntity<Object> getFinishedCard(@PathVariable long id, @PathVariable String hash) {
        log.info("Get finished card request");

        Card card = cardService.getCardAndCongratulationByCardId(id);
        if (card != null && card.getCardLink().equals(hash) && card.getStatus().equals(Status.ISOVER)) {
            log.info("Successfully writing card to response, id: {}", id);
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(card);
        } else {
            log.info("User has no access : {}", id);
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(Map.of("message", "Sorry, your link isn`t correct"));
        }
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
        log.info("Сard successfully created");
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(Map.of("id", cardService.createCard(card)));
    }

    @PutMapping("card/{id}/status/{statusName}")
    public void changeStatusAndCreateCardLink(@PathVariable long id, @PathVariable String statusName) {
        log.info("Received PUT request for change status");
        cardService.changeCardStatusAndCreateCardLink(statusName, id);
        log.info("Successfully changed card status for card id: {} to {}", id, statusName);
    }

    @DeleteMapping("card/{id}")
    public void delete(@PathVariable long id, HttpSession session) {
        log.info("Request for DELETE card");
        User user = (User) session.getAttribute("user");
        cardService.deleteCardById(id, user.getId());
    }

    @PutMapping("card/{id}/name")
    public void changeName(@RequestBody Card card,
                           @PathVariable long id,
                           HttpSession session) {
        log.info("Change name of card");
        User user = (User) session.getAttribute("user");
        card.setId(id);
        card.setUser(user);
        cardService.changeCardName(card);
    }

    @PutMapping("card/{id}/background")
    public void addBackground(@RequestParam Optional<MultipartFile> backgroundImage,
                              @RequestParam Optional<String> numberOfColor,
                              @PathVariable long id,
                              HttpSession session) {
        log.info("Add background to card");
        User user = (User) session.getAttribute("user");

        backgroundImage.ifPresent(file->cardService.saveBackground(id,user,file));
        numberOfColor.ifPresent(color -> cardService.saveBackgroundOfCongratulation(id,user,color));
    }

    @DeleteMapping("card/{id}/background")
    public void resetBackground(@PathVariable long id, HttpSession session) {
        log.info("Reset background to card");
        User user = (User) session.getAttribute("user");
        cardService.removeBackground(id,user);
    }
}
