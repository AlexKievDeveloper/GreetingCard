package com.greetingcard.service.impl;

import com.greetingcard.entity.*;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.CardService;
import com.greetingcard.service.CardUserService;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DefaultWebSocketService implements WebSocketService {
    private CardService cardService;
    private CongratulationService congratulationService;
    private CardUserService cardUserService;
    private SecurityService securityService;
    private SimpMessagingTemplate template;

    @Override
    public void notifyAboutCardStatusChanging(long cardId, String statusName, User userLoggedIn) {
        List<UserInfo> users = cardUserService.getUsersByCardId(cardId, userLoggedIn);
        for (UserInfo user : users) {
            sendMessage("Admin changed status of card with id: " + cardId + " to: " + statusName, user.getId());
        }
    }

    @Override
    public void notifyAboutAddingToCard(String message, String login) {
        User user = securityService.findByLogin(login);
        sendMessage(message, user.getId());
    }

    @Override
    public void notifyAdmin(String message, long cardId) {
        Card card = cardService.getCardAndCongratulationByCardId(cardId);
        long userId = card.getUser().getId();
        sendMessage(message, userId);
    }

    @Override
    public void notifyAllCardMembersAboutDeletingCongratulation(long congratulationId, User userLoggedIn) {
        Congratulation congratulation = congratulationService.getCongratulationById(congratulationId);
        log.info("User logged in! " + userLoggedIn);

        List<UserInfo> users = cardUserService.getUsersByCardIdForWebSocketNotification(congratulation.getCardId(), userLoggedIn);
        for (UserInfo user : users) {
            log.info("User recipient of notifications" + user);
            sendMessage(userLoggedIn.getLogin() + " deleted congratulation with id: " + congratulationId +
                    " in card with id: " + congratulation.getCardId(), user.getId());
        }
    }

    @Override
    public void notifyAllDeletedCardMembers(List<UserInfo> listUsers, long cardId) {
        for (UserInfo user : listUsers) {
            sendMessage("Admin removed you from card with id: " + cardId, user.getId());
        }
    }

    void sendMessage(String message, long userId) {
        template.convertAndSend("/topic/" + userId, new WebResponse(message));
    }

}
