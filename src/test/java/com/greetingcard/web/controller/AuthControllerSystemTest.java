package com.greetingcard.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml", "forgot_password_hashes.xml", "verify_email_hashes.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerSystemTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
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
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.userId").value("2"))
                .andExpect(jsonPath("$.userLanguage").value("UA"))
                .andReturn().getResponse();

        String headerValue = response.getHeader("Authorization");
        assertNotNull(headerValue);
        assertTrue(headerValue.startsWith("Bearer "));
    }

    @Test
    @DisplayName("Login user if login didn't create")
    void testLoginIfUserIsNotExist() throws Exception {
        //prepare
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("login", "user_don't_create");
        userCredential.put("password", "user");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userCredential);

        //when
        mockMvc.perform(post("/api/v1/auth")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Access denied. Please check your login and password"));
    }

    @Test
    @DisplayName("Login user with Facebook")
    void testLoginWithFacebook() throws Exception {
        //prepare
        Map<String, String> facebookCredential = new HashMap<>();
        facebookCredential.put("name", "Roma Roma");
        facebookCredential.put("email", "userFacebook");
        facebookCredential.put("userID", "userFacebook");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(facebookCredential);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth/facebook")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("userFacebook"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andReturn().getResponse();

        String headerValue = response.getHeader("Authorization");
        assertNotNull(headerValue);
        assertTrue(headerValue.startsWith("Bearer "));
    }

    @Test
    @DisplayName("Login user with Google")
    void testLoginWithGoogle() throws Exception {
        //prepare
        Map<String, String> googleCredential = new HashMap<>();
        googleCredential.put("googleId", "Roma Roma");
        googleCredential.put("imageUrl", "user");
        googleCredential.put("email", "userGoogle");
        googleCredential.put("name", "user");
        googleCredential.put("givenName", "user");
        googleCredential.put("familyName", "user");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(googleCredential);

        //when
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/auth/google")
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("userGoogle"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andReturn().getResponse();

        String headerValue = response.getHeader("Authorization");
        assertNotNull(headerValue);
        assertTrue(headerValue.startsWith("Bearer "));
    }

}
