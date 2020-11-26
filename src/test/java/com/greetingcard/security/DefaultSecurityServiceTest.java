package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringJUnitWebConfig(value = FlywayConfig.class)
@ExtendWith(MockitoExtension.class)
class DefaultSecurityServiceTest {
    @Autowired
    private DefaultSecurityService securityService;
    @InjectMocks
    private DefaultSecurityService mockSecurityService;
    @Mock
    private UserDao userDao;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void init() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("Login user")
    void loginTest() {
        //when
        User actual = securityService.login("user", "user");

        //then
        assertEquals("user", actual.getLogin());
        assertEquals("user", actual.getFirstName());
        assertEquals("user", actual.getLastName());
        assertEquals("@user", actual.getEmail());
        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", actual.getPassword());
        assertEquals("salt", actual.getSalt());
        assertEquals(Language.ENGLISH, actual.getLanguage());
    }

    @Test
    @DisplayName("Login user if login too long")
    void testLoginIfLoginTooLong() {
        assertThrows(IllegalArgumentException.class, () ->
                securityService.login("logintoooooooooooooooooooooloooooooooooooooooooooonggggg", "user"));
    }

    @Test
    @DisplayName("Returns hashed password")
    void getHashPasswordTest() {
        //prepare
        String salt = "salt";
        String testPassword = "user";
        String expectedPassword = "gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=";
        //when
        String actualPassword = securityService.getHashPassword(salt.concat(testPassword));
        //then
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    @DisplayName("Find user by id")
    void findById() {
        //when
        mockSecurityService.findById(1);
        //then
        verify(userDao).findById(1);
    }

    @Test
    @DisplayName("Update user by id")
    void updateUser() throws IOException {
        Files.createDirectories(Path.of("src/test/java/file/pathToUserPhoto"));
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg",
                "image/jpg", "test-image.jpg".getBytes());
        User user = User.builder()
                .id(100L)
                .firstName("test")
                .lastName("test")
                .login("test")
                .pathToPhoto("testFile").build();
        //when
        securityService.update(user, file);
        //then
        assertNotEquals("testFile",user.getPathToPhoto());
    }
}