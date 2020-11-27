package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.jdbc.FlywayConfig;
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

@SpringJUnitWebConfig(FlywayConfig.class)
@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@DBRider
@DataSet(cleanBefore = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardUserControllerSystemTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    private static final String URL_ADD_MEMBER = "/api/v1/card/{id}/user";

    @BeforeAll
    void createDB() {
        flyway.clean();
        flyway.migrate();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Add member to card - login is empty")
    @DataSet("cardUsers.xml")
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
    @DataSet("cardUsers.xml")
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
    @DataSet("cardUsers.xml")
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
    @DataSet("cardUsers.xml")
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
    @DataSet("cardUsers.xml")
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
    @DataSet("cardUsers.xml")
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

    @AfterAll
    public void cleanUp() {
        flyway.clean();
    }
}