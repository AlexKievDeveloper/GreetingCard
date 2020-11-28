package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringJUnitWebConfig(TestConfiguration.class)
@DBRider
@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml",  "types.xml", "roles.xml",  "statuses.xml", "users.xml",  "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 3);",
        cleanAfter = true)
class CardUserControllerSystemTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    private final String URL_ADD_MEMBER = "/api/v1/card/{id}/user";

    @BeforeEach
    void createDB() {
        flyway.migrate();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Add member to card - login is empty")
    void addUserMemberIfEmptyLogin() throws Exception {
        User user = User.builder().id(2).build();
        String json = "{\"login\":\"\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Login of user is empty"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Add member to card - login does not exist")
    void addUserMemberIfNotExistingLogin() throws Exception {
        User user = User.builder().id(2).build();
        String json = "{\"login\":\"u1111\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Login does not exist"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - user is already added")
    void addUserMemberIfAlreadyMember() throws Exception {
        User user = User.builder().id(1).build();
        String json = "{\"login\":\"admin\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("User is already member"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - user is not admin for card")
    void addUserMemberIfNotAdminAddsMember() throws Exception {
        User user = User.builder().id(2).build();
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Only card owner can add users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - card is over")
    void addUserMemberCardIsOver() throws Exception {
        User user = User.builder().id(2).build();
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 2)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Card is already finished"))
                .andExpect(status().isBadRequest());
        ;
    }

    @Test
    @DisplayName("Add member to card - success")
    @ExpectedDataSet("cardUsersAdded.xml")
    void addUserMember() throws Exception {
        User user = User.builder().id(1).build();
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }
}