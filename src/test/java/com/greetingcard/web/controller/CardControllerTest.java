package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

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
        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(get("/api/v1/card/{id}", 1))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.name").value("greeting Nomar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return card with status ISOVER and full card_link by card_id and user_id")
    void getISoverCard() throws Exception {
        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(get("/api/v1/card/{id}", 2))
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
        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(get("/api/v1/card/{id}/card_link/{hash}", 2, "link_to_greeting"))
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
        TestWebUtils.loginAsUserId(3);
        mockMvc.perform(get("/api/v1/card/{id}", 1))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Sorry, you do not have access rights to the card or the card does not exist"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create card")
    void createCard() throws Exception {
        String json = "{\"name\":\"test\"}";
        TestWebUtils.loginAsUserId(2);

        mockMvc.perform(post("/api/v1/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Change status of card")
    void doPut_ISOVER() throws Exception {
        User user = User.builder().id(1).login("user").build();
        TestWebUtils.loginAsUser(user);
        mockMvc.perform(put("/api/v1/card/{id}/status/{statusName}", 1,"ISOVER")
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Change status of card to STARTUP")
    void doPut_STARTUP() throws Exception {
        User user = User.builder().id(1).login("user").build();
        TestWebUtils.loginAsUser(user);
        mockMvc.perform(put("/api/v1/card/{id}/status/{statusName}", 1,"STARTUP")
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete card")
    void doDelete() throws Exception {
        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(delete("/api/v1/card/{id}", 1)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return all card of user")
    void getCardsAll() throws Exception {

        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(get("/api/v1/cards?type=ALL"))
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
        TestWebUtils.loginAsUserId(1);
        String json = "{\"name\":\"newName\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Change the name of the card to long")
    void changeNameToLong() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "{\"name\":\"NewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar" +
                "NewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecarNewnamecar\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Change the name of the card to short")
    void changeNameToShort() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "{\"name\":\"\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Name is empty or too long"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update background")
    void addBackground() throws Exception {
        TestWebUtils.loginAsUserId(1);
        MockMultipartFile file = new MockMultipartFile("backgroundCardFile", "image.jpg",
                "image/jpg", "test-image.jpg".getBytes());
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/card/{id}/background",1);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mockMvc.perform(builder
                .file(file)
                .param("backgroundColorCongratulations", "11111")
                .param("backgroundCard","http://")
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update only background of congratulation")
    void addBackgroundWithOutFile() throws Exception {
        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(put("/api/v1/card/{id}/background",1)
                .param("backgroundColorCongratulations", "11111")
                .param("backgroundCard","http://")
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Reset background of card")
    void addBackgroundOfCard() throws Exception {
        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(put("/api/v1/card/{id}/background",1)
                .param("backgroundColorCongratulations", "11111")
                .param("backgroundCard","")
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("set finish of the card")
    void setTimeOfFinish() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "{\"dateOfFinish\":\"1985-01-25\"}";

        mockMvc.perform(put("/api/v1/card/{id}/date", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }

}