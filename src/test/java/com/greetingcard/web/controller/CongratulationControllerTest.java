package com.greetingcard.web.controller;

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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@DBRider
@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
class CongratulationControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.migrate();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Creating new congratulation from json which we get from request body")
    void createCongratulation() throws Exception {
        User user = User.builder().id(1).build();
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", "test-image.jpg".getBytes());
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", "test-audio.mp3".getBytes());
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\", \"image_links\":\"https://www.davno.ru/assets/images/cards/big/birthday-1061.jpg\"}";
        //String parametersJson = {message:Happy new year!, card_id:1, youtube:https://www.youtube.com/watch?v=BmBr5diz8WA, image_links:https://www.davno.ru/assets/images/cards/big/birthday-1061.jpg}

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/congratulation")
                .file(mockImageFile)
                .file(mockAudioFile)
                .param("json", parametersJson)
                .characterEncoding("utf-8")
                .sessionAttr("user", user)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Return bad request while creating congratulation in case to long value of youtube link")
    void createCongratulationExceptionOfToLongYoutubeLinkValue() throws Exception {
        User user = User.builder().id(1).build();
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", "test-image.jpg".getBytes());
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", "test-audio.mp3".getBytes());
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\", " +
                "\"image_links\":\"https://www.davno.ru/assets/images/cards/big/birthday-1061.jpg\"}";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/congratulation")
                .file(mockImageFile)
                .file(mockAudioFile)
                .param("json", parametersJson)
                .sessionAttr("user", user)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
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


