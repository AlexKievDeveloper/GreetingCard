package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;


@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('cards_card_id_seq', 3); SELECT setval(' users_cards_users_cards_id_seq', 6);",
        cleanAfter = true)
@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @BeforeEach
    void setMockMvc() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Return card by card_id and user_id")
    void getCard() throws Exception {
        User user = User.builder().id(2).build();
        mockMvc.perform(get("/api/v1/card/{id}", 1)
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.name").value("greeting Nomar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return card with status ISOVER and full card_link by card_id and user_id")
    void getISoverCard() throws Exception {
        User user = User.builder().id(2).build();
        mockMvc.perform(get("/api/v1/card/{id}", 2)
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.user.id").value("2"))
                .andExpect(jsonPath("$.name").value("greeting Oleksandr"))
                .andExpect(jsonPath("$.cardLink").value("https://greeting-team.herokuapp.com/card/2/card_link/link_to_greeting"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return finished card by card_id and user_id with hash only as link_to_greeting(only for showing to birthday boy)")
    void getFinishedCard() throws Exception {
        User user = User.builder().id(2).build();
        mockMvc.perform(get("/api/v1/card/{id}/card_link/{hash}", 2, "link_to_greeting")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.user.id").value("2"))
                .andExpect(jsonPath("$.name").value("greeting Oleksandr"))
                .andExpect(jsonPath("$.cardLink").value("link_to_greeting"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return message when user is not a member of this card")
    void getCardNoAccessOrCard() throws Exception {
        User user = User.builder().id(-1).build();
        mockMvc.perform(get("/api/v1/card/{id}", 1)
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Sorry, you are not a member of this card"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create card")
    void createCard() throws Exception {
        String json = "{\"name\":\"test\"}";
        User user = User.builder().id(2).build();

        mockMvc.perform(post("/api/v1/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Change status of card")
    void doPut() throws Exception {
        mockMvc.perform(put("/api/v1/card/{id}/status", 1)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete card")
    void doDelete() throws Exception {
        User user = User.builder().id(1).build();
        mockMvc.perform(delete("/api/v1/card/{id}", 1)
                .sessionAttr("user", user)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return all card of user")
    void getCardsAll() throws Exception {
        User user = User.builder().id(1).build();
        mockMvc.perform(get("/api/v1/cards?type=all")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("greeting Nomar"))
                .andExpect(jsonPath("$[0].status").value("STARTUP"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("greeting Oleksandr"))
                .andExpect(jsonPath("$[1].status").value("ISOVER"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[2].name").value("no_congratulation"))
                .andExpect(jsonPath("$[2].status").value("STARTUP"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Change the name of the card")
    void changeName() throws Exception {
        User user = User.builder().id(1).build();
        String json = "{\"name\":\"newName\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Change the name of the card to long")
    void changeNameToLong() throws Exception {
        User user = User.builder().id(1).build();
        String json = "{\"name\":\"NewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Change the name of the card to short")
    void changeNameToShort() throws Exception {
        User user = User.builder().id(1).build();
        String json = "{\"name\":\"\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Name is short or too long"))
                .andExpect(status().isBadRequest());
    }

}