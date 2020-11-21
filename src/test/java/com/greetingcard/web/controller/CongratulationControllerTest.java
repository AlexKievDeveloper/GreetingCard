package com.greetingcard.web.controller;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
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

@SpringJUnitWebConfig(value = FlywayConfig.class)
class CongratulationControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private Flyway flyway;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Creating new congratulation from json which we get from request body")
    void createCongratulation() throws Exception {
        User user = User.builder().id(1).build();
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\", \"plain_link\":\"https://www.studytonight.com/servlet/httpsession.php\"}";
        mockMvc.perform(post("/api/v1/congratulation")
                .content(parametersJson)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Return bad request while creating congratulation in case to long value of plain link")
    void createCongratulationExceptionOfToLongPlainLinkValue() throws Exception {
        User user = User.builder().id(1).build();
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\", " +
                "\"plain_link\":\"https://www.studytonight.com/servlet/httpsession.phppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "\"}";
        mockMvc.perform(post("/api/v1/congratulation")
                .content(parametersJson)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Sorry, congratulation not saved. The link is very long. Please use a link up to 500 characters."))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Return bad request while creating congratulation in case to long value of youtube link")
    void createCongratulationExceptionOfToLongYoutubeLinkValue() throws Exception {
        User user = User.builder().id(1).build();
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp\", " +
                "\"plain_link\":\"https://www.studytonight.com/servlet/httpsession.php\"}";
        mockMvc.perform(post("/api/v1/congratulation")
                .content(parametersJson)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Wrong youtube link url!"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Changing congratulation status")
    void changeCongratulationStatus() throws Exception {
        mockMvc.perform(put("/api/v1/congratulation/{id}/status", 1))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deleting congratulation")
    void deleteCongratulation() throws Exception {
        User user = User.builder().id(1).login("user").build();
        mockMvc.perform(delete("/api/v1/congratulation/{id}", 1)
                .sessionAttr("user", user))
                .andExpect(status().isNoContent());
    }
}


