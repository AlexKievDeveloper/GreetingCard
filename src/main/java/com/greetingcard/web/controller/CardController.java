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
    public ResponseEntity<Object> getCards(@RequestParam CardsType type) {
        log.info("Get cards request");
        long userId = WebUtils.getCurrentUserId();
        List<Card> cardList = cardService.getCards(userId, type);
        return ResponseEntity.status(HttpStatus.OK).body(cardList);
    }

    @GetMapping("card/{id}")
    public ResponseEntity<Object> getCard(@PathVariable long id) {
        log.info("Get card request");
        long userId = WebUtils.getCurrentUserId();

        Card card = cardService.getCardAndCongratulationByCardIdAndUserId(id, userId);

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

    @GetMapping("card/{id}/generate_card_link/")
    public ResponseEntity<Object> getGeneratedCardLink(@PathVariable long id) {
        log.info("Get generated card link request");
        String hash = cardService.generateCardLink(id);
        String link = siteUrl + "invite_link/" + id + "/code/"+ hash;
        //@GetMapping("card/{id}/invite_card_link/") должна вести на страницу логина внутри с айди карты
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("invite_link", link));
    }

    @PostMapping(value = "card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createCard(@RequestBody Card card) {
        log.info("Creating card request");
        int length = card.getName().length();
        if (length == 0 || length > 250) {
            throw new IllegalArgumentException("Name is short or too long");
        }
        User user = WebUtils.getCurrentUser();
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
    public void delete(@PathVariable long id) {
        log.info("Request for DELETE card");
        long userId = WebUtils.getCurrentUserId();
        cardService.deleteCardById(id, userId);
    }

    @PutMapping("card/{id}/name")
    public void changeName(@RequestBody Card card, @PathVariable long id) {
        User user = WebUtils.getCurrentUser();
        card.setId(id);
        card.setUser(user);
        cardService.changeCardName(card);
    }

    @PutMapping("card/{id}/background")
    public void addBackground(@RequestParam Optional<MultipartFile> backgroundCardFile,
                              @RequestParam String backgroundColorCongratulations,
                              @RequestParam String backgroundCard,
                              @PathVariable long id) {
        log.info("Add background to card");
        long userId = WebUtils.getCurrentUserId();

        if (backgroundCard.isBlank()) {
            backgroundCardFile.ifPresent(file -> cardService.saveBackground(id, userId, file));
        } else {
            cardService.removeBackground(id, userId);
        }
        cardService.saveBackgroundOfCongratulation(id, userId, backgroundColorCongratulations);
    }

}
