package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.web.security.jwt.JwtProvider;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CardControllerSystemSecurityTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @BeforeEach
    void setMockMvc() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(sharedHttpSession())
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Return card by card_id and user_id")
    void getCard() throws Exception {
        String token = jwtProvider.generateToken("user");
        mockMvc.perform(get("/api/v1/card/{id}", 1)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.name").value("greeting Nomar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return card by card_id and user_id without token")
    void getCardWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/card/{id}", 1))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Change the name of the card")
    void changeName() throws Exception {
        String token = jwtProvider.generateToken("admin");
        String json = "{\"name\":\"newName\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Change the name of the card without token")
    void changeNameWithoutToken() throws Exception {
        String json = "{\"name\":\"newName\"}";

        mockMvc.perform(put("/api/v1/card/{id}/name", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }
}
