package com.greetingcard.web.controller;

import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileControllerTest {
    private MockMvc mockMvc;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Return user from session")
    void getUser() throws Exception {
        User user = User.builder().id(2)
                .login("test").firstName("test")
                .lastName("test").email("test")
                .password("password").salt("salt")
                .pathToPhoto("link").build();
        TestWebUtils.loginAsUser(user);
        mockMvc.perform(get("/api/v1/user"))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.firstName").value("test"))
                .andExpect(jsonPath("$.lastName").value("test"))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.email").value(nullValue(String.class)))
                .andExpect(jsonPath("$.password").value(nullValue(String.class)))
                .andExpect(jsonPath("$.salt").value(nullValue(String.class)))
                .andExpect(jsonPath("$.google").value(nullValue(String.class)))
                .andExpect(jsonPath("$.facebook").value(nullValue(String.class)))
                .andExpect(jsonPath("$.pathToPhoto").value("link"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update field of user")
    void updateUser() throws Exception {
        TestWebUtils.loginAsUserId(1);
        MockMultipartFile file = new MockMultipartFile("profileFile", "image.jpg",
                "image/jpg", "test-image.jpg".getBytes());
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/user");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mockMvc.perform(builder
                .file(file)
                .param("firstName", "firstName")
                .param("lastName", "parametersJson")
                .param("login", "parametersJson")
                .param("pathToPhoto", "parametersJson")
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Request to change language")
    @ExpectedDataSet("usersAfterChangeLanguage.xml")
    void testChangeLanguage() throws Exception {
        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(put("/api/v1/user/language/ENGLISH"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}