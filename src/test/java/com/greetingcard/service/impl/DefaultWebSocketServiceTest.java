package com.greetingcard.service.impl;

import com.greetingcard.entity.*;
import com.greetingcard.security.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultWebSocketServiceTest {
    @Mock
    private DefaultCardService cardService;
    @Mock
    private DefaultCongratulationService congratulationService;
    @Mock
    private DefaultCardUserService cardUserService;
    @Mock
    private SecurityService securityService;
    @Mock
    private SimpMessagingTemplate template;
    @InjectMocks
    private DefaultWebSocketService webSocketService;

    @Test
    @DisplayName("Notify about status changing")
    void notifyAboutCardStatusChanging() {
        //prepare
        User user = User.builder().id(1).login("user").build();
        List<UserInfo> userInfoList = List.of(UserInfo.builder().id(1).login("user").build());
        when(cardUserService.getUsersByCardId(1, user)).thenReturn(userInfoList);
        doNothing().when(template).convertAndSend(any(String.class), any(WebResponse.class));

        //when
        webSocketService.notifyAboutCardStatusChanging(1, "ISOVER", user);

        //then
        verify(cardUserService).getUsersByCardId(1, user);
        verify(template).convertAndSend(any(String.class), any(WebResponse.class));
    }


    @Test
    @DisplayName("Notify about adding to card")
    void notifyAboutAddingToCard() {
        //prepare
        User user = User.builder().id(1).login("user").build();
        when(securityService.findByLogin("user")).thenReturn(user);
        doNothing().when(template).convertAndSend(any(String.class), any(WebResponse.class));

        //when
        webSocketService.notifyAboutAddingToCard("test", "user");

        //then
        verify(securityService).findByLogin("user");
        verify(template).convertAndSend(any(String.class), any(WebResponse.class));
    }

    @Test
    @DisplayName("Notify Admin")
    void notifyAdmin() {
        //prepare
        User user = User.builder().id(1).login("user").build();
        Card card = Card.builder().id(1).build();
        card.setUser(user);
        when(cardService.getCardAndCongratulationByCardId(1)).thenReturn(card);
        doNothing().when(template).convertAndSend(any(String.class), any(WebResponse.class));

        //when
        webSocketService.notifyAdmin("test", 1);

        //then
        verify(cardService).getCardAndCongratulationByCardId(1);
        verify(template).convertAndSend(any(String.class), any(WebResponse.class));
    }

    @Test
    @DisplayName("Notify all card members about deleting congratulation")
    void notifyAllCardMembersAboutDeletingCongratulation() {
        //prepare
        User user = User.builder().id(1).login("user").build();
        Congratulation congratulation = Congratulation.builder().cardId(1).build();
        List<UserInfo> userInfoList = List.of(UserInfo.builder().id(1).login("user").build());
        when(congratulationService.getCongratulationById(1)).thenReturn(congratulation);
        when(cardUserService.getUsersByCardIdForWebSocketNotification(1, user)).thenReturn(userInfoList);
        doNothing().when(template).convertAndSend(any(String.class), any(WebResponse.class));

        //when
        webSocketService.notifyAllCardMembersAboutDeletingCongratulation(1, user);

        //then
        verify(congratulationService).getCongratulationById(1);
        verify(cardUserService).getUsersByCardIdForWebSocketNotification(1, user);
        verify(template).convertAndSend(any(String.class), any(WebResponse.class));
    }

    @Test
    @DisplayName("Notify all deleted card members")
    void notifyAllDeletedCardMembers() {
        //prepare
        List<UserInfo> userInfoList = List.of(UserInfo.builder().id(1).login("user").build());
        doNothing().when(template).convertAndSend(any(String.class), any(WebResponse.class));

        //when
        webSocketService.notifyAllDeletedCardMembers(userInfoList, 1);

        //then
        verify(template).convertAndSend(any(String.class), any(WebResponse.class));
    }

    @Test
    @DisplayName("")
    void sendMessage() {
        //when
        webSocketService.sendMessage("test", 1);

        //then
        verify(template).convertAndSend("/topic/1", new WebResponse("test"));
    }
}