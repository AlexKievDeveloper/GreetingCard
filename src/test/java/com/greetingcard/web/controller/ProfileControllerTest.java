package com.greetingcard.web.controller;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class ProfileControllerTest {
    private MockMvc mockMvcForCreateCongratulation;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mockMvcForCreateCongratulation = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Return user from session")
    void getUser() throws Exception {
        User user = User.builder().id(2)
                .login("test")
                .firstName("test")
                .lastName("test")
                .email("test")
                .pathToPhoto("link").build();
        mockMvcForCreateCongratulation.perform(get("/api/v1/user")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.email").value("test"))
                .andExpect(jsonPath("$.password").value(nullValue(String.class)))
                .andExpect(jsonPath("$.salt").value(nullValue(String.class)))
                .andExpect(jsonPath("$.google").value(nullValue(String.class)))
                .andExpect(jsonPath("$.facebook").value(nullValue(String.class)))
                .andExpect(jsonPath("$.pathToPhoto").value("link"))
                .andExpect(status().isOk());

    }
}