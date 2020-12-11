package com.greetingcard.security;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.service.EmailService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static com.greetingcard.entity.AccessHashType.FORGOT_PASSWORD;
import static com.greetingcard.entity.AccessHashType.VERIFY_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;


@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml", "forgot_password_hashes.xml", "verify_email_hashes.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 3);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceSystemTest {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserDao userDao;

    @MockBean
    private EmailService emailService;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
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
        assertEquals(Language.UKRAINIAN, actual.getLanguage());
    }

    @Test
    @DisplayName("Login user if login too long")
    void testLoginIfLoginTooLong() {
        assertThrows(IllegalArgumentException.class, () ->
                securityService.login("logintoooooooooooooooooooooloooooooooooooooooooooonggggg", "user"));
    }

    @Test
    @DisplayName("Update user by id")
    void updateUser() {
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
        assertNotEquals("testFile", user.getPathToPhoto());
    }

    @Test
    @DisplayName("Update user's password")
    @ExpectedDataSet("usersAfterChangePassword.xml")
    void testUpdatePassword() {
        //prepare
        User user = User.builder()
                .id(2L)
                .password("newPassword")
                .salt("salt")
                .build();
        //when
        securityService.updatePassword(user);
    }

    @Test
    @DisplayName("Restore password")
    void testRestorePassword() {
        //when
        securityService.restorePassword("testEmail");
        //then
        verify(emailService).sendMail(anyString(), anyString(), anyString());
        User user = userDao.findByEmail("testEmail");
        assertEquals(44, user.getPassword().length());
    }

    @Test
    @DisplayName("Verify the access hash")
    @ExpectedDataSet(value= {"usersAfterVerifyEmail.xml", "verify_email_hashesAfterCheckingHash.xml"})
    void testVerifyEmailAccessHash() {
        securityService.verifyEmailAccessHash("accessHash");
    }

    @Test
    @DisplayName("Verify the access hash")
    @ExpectedDataSet(value = {"usersAfterChangePassword.xml", "forgot_password_hashesAfterCheckingHash.xml"})
    void testVerifyForgotPasswordAccessHash() {
        securityService.verifyForgotPasswordAccessHash("accessHash", "newPassword");
    }

    @Test
    @DisplayName("Generate an access hash, based on user's email + random salt")
    void testGenerateVerifyEmailAccessHash() {
        //when
        String newHash = securityService.generateAccessHash("@user", VERIFY_EMAIL);
        //then
        assertNotNull(newHash);
    }

    @Test
    @DisplayName("Generate an access hash, based on user's email + random salt")
    void testGenerateForgotPasswordAccessHash() {
        //when
        String newHash = securityService.generateAccessHash("@user", FORGOT_PASSWORD);
        //then
        assertNotNull(newHash);
    }
}
