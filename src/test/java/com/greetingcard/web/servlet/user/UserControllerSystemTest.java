package com.greetingcard.web.servlet.user;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.dto.UserCredential;
import com.greetingcard.entity.User;
import com.greetingcard.web.controller.UserController;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class UserControllerSystemTest {

    @Autowired
    private UserController userController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Invalidates the session")
    void doDelete() {
        //prepare

    }

    @Test
    @DisplayName("Login user and set User as session attribute ")
    void doPostTestUserExist() throws Exception {
        UserCredential userCredential = new UserCredential("user", "user");
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"user\":\"user\", \"password\":\"user\"}";//mapper.writeValueAsString(userCredential);

        HttpSession result = mockMvc.perform(post("/api/v1/session")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession();
        User user = (User) result.getAttribute("user");
        user.getId();
    }

    @Test
    @DisplayName("Returns message: Access denied. Please login and try again.")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void doPostTestUserIsNull() {
        //prepare

    }
}
