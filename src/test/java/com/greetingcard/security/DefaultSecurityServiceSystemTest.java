package com.greetingcard.security;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
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

import java.util.HashMap;
import java.util.Map;

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
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceSystemTest {
    @Autowired
    private DefaultSecurityService securityService;

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
    @DisplayName("Throws exception when user with requested login does not exist")
    void testFindUserByLoginIfLoginNotFound() {
        //when + then
        IllegalAccessError e = assertThrows(IllegalAccessError.class, () -> securityService.login("user_is_not_exist", "not_exist"));
        assertEquals("Access denied. Please check your login and password", e.getMessage());
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
    @DisplayName("Throws exception when user with requested login does not exist")
    void testFindByLoginIfLoginNotFound() {
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> securityService.findByLogin("user_is_not_exist"));
        assertEquals("Login does not exist", e.getMessage());
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
    @ExpectedDataSet(value = {"usersAfterVerifyEmail.xml", "verify_email_hashesAfterCheckingHash.xml"})
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

    @Test
    @DisplayName("Find user by login")
    void testFindByLogin() {
        //prepare
        String userName = "user";
        //when
        User user = securityService.findByLogin(userName);
        //then
        assertEquals(2L, user.getId());
        assertEquals("user", user.getFirstName());
        assertEquals("user", user.getLastName());
        assertEquals("user", user.getLogin());
        assertEquals("@user", user.getEmail());
        assertEquals("testPathToPhoto2", user.getPathToPhoto());
        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", user.getPassword());
        assertEquals("salt", user.getSalt());
        assertEquals(Language.UKRAINIAN, user.getLanguage());
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
    @DisplayName("Login user from facebook")
    void loginWithFacebook() {
        //prepare
        Map<String, String> facebookCredential = new HashMap<>();
        facebookCredential.put("name", "Roma Roma");
        facebookCredential.put("email", "@user");
        facebookCredential.put("userID", "userFacebook");
        //when
        User actualUser = securityService.loginWithFacebook(facebookCredential);
        //then
        assertNull(actualUser.getFacebook());
        assertNull(actualUser.getGoogle());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("@user", actualUser.getEmail());
        assertEquals("salt", actualUser.getSalt());
    }

    @Test
    @DisplayName("Login user from google")
    void loginWithGoogle() {
        //prepare
        Map<String, String> googleCredential = new HashMap<>();
        googleCredential.put("googleId", "Roma Roma");
        googleCredential.put("imageUrl", "@user");
        googleCredential.put("email", "@user");
        googleCredential.put("name", "user");
        googleCredential.put("givenName", "user");
        googleCredential.put("familyName", "user");
        //when
        User actualUser = securityService.loginWithGoogle(googleCredential);
        //then
        assertNull(actualUser.getFacebook());
        assertNull(actualUser.getGoogle());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("@user", actualUser.getEmail());
        assertEquals("salt", actualUser.getSalt());
    }
}
