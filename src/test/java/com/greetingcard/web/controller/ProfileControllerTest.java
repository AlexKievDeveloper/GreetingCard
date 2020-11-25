package com.greetingcard.web.controller;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class ProfileControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void init() {
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
        mockMvc.perform(get("/api/v1/user")
                .sessionAttr("user", user))
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
        Files.createDirectories(Path.of("src/test/java/file/pathToUserPhoto"));
        User user = User.builder().id(1).build();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg",
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
                .sessionAttr("user", user)
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
