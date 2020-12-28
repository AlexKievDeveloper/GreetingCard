package com.greetingcard.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.service.WebSocketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.net.http.WebSocket;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@PropertySource("classpath:application.properties")
class DefaultWebSocketServiceTest {

    @Autowired
    private WebSocketService webSocketService;

    @Test
    @DisplayName("")
    void notifyAboutCardStatusChanging() {
    }

    @Test
    @DisplayName("")
    void notifyAboutGettingCards() {
    }

    @Test
    @DisplayName("")
    void notifyAboutAddingToCard() {
    }

    @Test
    @DisplayName("")
    void notifyAdminAboutLeavingCard() {
    }

    @Test
    @DisplayName("")
    void notifyAdminAboutCreatingCongratulation() {
    }

    @Test
    @DisplayName("")
    void notifyAllCardMembersAboutDeletingCongratulation() throws JsonProcessingException {
        //prepare
        User user = User.builder().id(1).login("user").build();
        //when
        webSocketService.notifyAllCardMembersAboutDeletingCongratulation(1, user);
        //then

    }

    @Test
    @DisplayName("")
    void notifyAllDeletedCardMembers() {
    }

    @Test
    @DisplayName("")
    void sendMessage() {
    }
}