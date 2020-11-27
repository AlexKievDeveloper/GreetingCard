package com.greetingcard.web.controller;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.User;
import com.greetingcard.service.impl.DefaultCongratulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

@SpringJUnitWebConfig(value = FlywayConfig.class)
class CongratulationControllerTest {
    private MockMvc mockMvcForCreateCongratulation;
    private MockMvc mockMvc;
    private final byte[] bytes = new byte[1024 * 1024 * 10];

    @Mock
    private DefaultCongratulationService defaultCongratulationService;
    @InjectMocks
    private CongratulationController mockCongratulationController;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Creating new congratulation from json which we get from request body")
    void createCongratulation() throws Exception {
        mockMvcForCreateCongratulation = MockMvcBuilders.standaloneSetup(mockCongratulationController).apply(sharedHttpSession()).build();
        User user = User.builder().id(1).build();
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\"}";

        mockMvcForCreateCongratulation.perform(MockMvcRequestBuilders.multipart("/api/v1/congratulation")
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
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}";

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
