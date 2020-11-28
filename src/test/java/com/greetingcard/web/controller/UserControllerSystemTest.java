package com.greetingcard.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml",  "types.xml", "roles.xml",  "statuses.xml", "users.xml",  "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 3);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
class UserControllerSystemTest {
    @Autowired
    private UserController userController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.migrate();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Invalidates the session")
    void testLogout() throws Exception {
        //prepare
        User user = User.builder()
                .login("user")
                .password("user").build();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        assertNotNull(session.getAttribute("user"));
        //when
        mockMvc.perform(delete("/api/v1/session").session(session))
                .andDo(print())
                .andExpect(status().isOk());
        assertNotNull(session);
        assertThrows(IllegalStateException.class, () -> session.getAttribute("user"));
    }

    @Test
    @DisplayName("Login user")
    void testLoginIfUserExist() throws Exception {
        //prepare
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("login", "user");
        userCredential.put("password", "user");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userCredential);

        //when
        HttpSession result = mockMvc.perform(post("/api/v1/session")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.userId").value("2"))
                .andReturn().getRequest().getSession();

        //then
        assertNotNull(result);
        User user = (User) result.getAttribute("user");
        assertNotNull(user);
        assertEquals("user", user.getLogin());
    }

    @Test
    @DisplayName("Login user if login didn't create")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testLoginIfUserIsNotExist() throws Exception {
        //prepare
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("login", "user_don't_create");
        userCredential.put("password", "user");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userCredential);

        //when
        HttpSession result = mockMvc.perform(post("/api/v1/session")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Access denied. Please check your login and password"))
                .andReturn().getRequest().getSession();
        //then
        assertNotNull(result);
        User user = (User) result.getAttribute("user");
        assertNull(user);
    }

    @Test
    @DisplayName("Registration new user")
    void testRegistration() throws Exception {
        //prepare
        String json = "{\n" +
                "  \"firstName\" : \"user\",\n" +
                "  \"lastName\" : \"user\",\n" +
                "  \"email\" : \"user@test\",\n" +
                "  \"login\" : \"user_test\",\n" +
                "  \"password\" : \"user\" \n" +
                "}";

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Registration new user if login too long")
    void testRegistrationIfLoginTooLong() throws Exception {
        //prepare
        String json = "{\n" +
                "  \"firstName\" : \"user\",\n" +
                "  \"lastName\" : \"user\",\n" +
                "  \"email\" : \"user@test\",\n" +
                "  \"login\" : \"usertooooooooooooooooooooooloooooooooooooooooooooooooooooooooong\",\n" +
                "  \"password\" : \"user\" \n" +
                "}";

        mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Sorry, login is too long. " +
                                "Please put login up to 50 characters."));
    }
}
