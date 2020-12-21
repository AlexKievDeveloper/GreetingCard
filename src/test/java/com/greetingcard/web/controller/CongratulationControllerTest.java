package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.impl.DefaultCongratulationService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CongratulationControllerTest {
    private MockMvc mockMvc;

    private final byte[] bytes = new byte[1024 * 1024];

    @MockBean
    private DefaultCongratulationService defaultCongratulationService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @BeforeEach
    void setMockMvc() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Creating new congratulation from json which we get from request body")
    void createCongratulation() throws Exception {
        User user = User.builder().id(1).build();
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\"}";

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

        when(defaultCongratulationService.getLinkList(any(), any(), any())).thenThrow(new IllegalArgumentException("Wrong youtube link url!"));

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
    @DisplayName("Returns congratulation by id")
    void getCongratulation() throws Exception {
        User user = User.builder().id(1).login("user").build();
        Congratulation congratulation = Congratulation.builder()
                .id(1)
                .status(Status.STARTUP)
                .cardId(1)
                .message("from Roma")
                .build();

        when(defaultCongratulationService.getCongratulationById(1)).thenReturn(congratulation);

        mockMvc.perform(get("/api/v1/congratulation/{id}", 1)
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("from Roma"))
                .andExpect(jsonPath("$.cardId").value(1))
                .andExpect(jsonPath("$.status").value("STARTUP"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Creating new congratulation from json which we get from request body")
    void editCongratulation() throws Exception {
        User user = User.builder().id(1).build();
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        String parametersJson = "{\"message\":\"Happy new year!\", \"card_id\":\"1\", \"youtube\":\"https://www.youtube.com/watch?v=BmBr5diz8WA\"}";

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/congratulation/{id}", 1);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
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

    @Test
    @DisplayName("Deleting links from congratulation")
    void deleteLinksById() throws Exception {
        User user = User.builder().id(1).login("user").build();
        String idsJson = "[{\"id\":\"1\", \"id\":\"2\"}]";
        mockMvc.perform(delete("/api/v1/congratulation/{id}/links", 1)
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(idsJson)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
