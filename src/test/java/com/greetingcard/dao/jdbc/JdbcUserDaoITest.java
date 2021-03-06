package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml", "forgot_password_hashes.xml", "verify_email_hashes.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {
    @Autowired
    private JdbcUserDao userDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @Test
    @DisplayName("Find user by login")
    void testFindUserByLogin() {
        //when
        User actualUser = userDao.findByLogin("user");
        //then
        assertNotNull(actualUser);
        assertEquals(2, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());

        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(Language.UKRAINIAN, actualUser.getLanguage());
    }

    @Test
    @DisplayName("Throws exception when user with requested login does not exist")
    void testFindUserByLoginIfLoginNotFound() {
        //when + then
        assertThrows(EmptyResultDataAccessException.class, () -> userDao.findByLogin("user_is_not_created"));
    }

    @Test
    @DisplayName("Save user")
    void testSave() {
        //prepare
        User expected = User.builder().firstName("firstName_test").lastName("lastName_test")
                .login("login_test").email("email_test").password("password").salt("salt")
                .language(Language.ENGLISH).build();
        //when
        userDao.save(expected);
        User actualUser = userDao.findByLogin("login_test");
        //then
        assertNotNull(actualUser);
        assertEquals("firstName_test", actualUser.getFirstName());
        assertEquals("lastName_test", actualUser.getLastName());
        assertEquals("login_test", actualUser.getLogin());
        assertEquals("email_test", actualUser.getEmail());
        assertEquals("password", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(Language.ENGLISH, actualUser.getLanguage());
    }

    @Test
    @DisplayName("Throws illegal argument exception if login already exists id DB")
    void testSaveLoginlAlreadyExists() {
        //prepare
        User expected = User.builder().firstName("firstName_test").lastName("lastName_test")
                .login("admin").email("email_test").password("password").salt("salt")
                .language(Language.ENGLISH).build();
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userDao.save(expected));
        assertEquals("User with the same login or email already exists. Please try another login or email.", e.getMessage());
    }

    @Test
    @DisplayName("Throws illegal argument exception if email already exists id DB")
    void testSaveEmailAlreadyExists() {
        //prepare
        User expected = User.builder().firstName("firstName_test").lastName("lastName_test")
                .login("login_test").email("@admin").password("password").salt("salt")
                .language(Language.ENGLISH).build();
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userDao.save(expected));
        assertEquals("User with the same login or email already exists. Please try another login or email.", e.getMessage());
    }

    @Test
    @DisplayName("Update user")
    void testUpdate() {
        //prepare
        User user = userDao.findByLogin("user");
        user.setFirstName("update");
        user.setLastName("update");
        user.setLogin("update");
        user.setPathToPhoto("src/main/webapp/static/updatePhoto");

        //when
        userDao.update(user);
        User actualUser = userDao.findByLogin("update");
        //then
        assertEquals("update", actualUser.getFirstName());
        assertEquals("update", actualUser.getLastName());
        assertEquals("update", actualUser.getLogin());
        assertEquals("src/main/webapp/static/updatePhoto", actualUser.getPathToPhoto());
    }

    @Test
    @DisplayName("Update user's password")
    @ExpectedDataSet("usersAfterChangePassword.xml")
    void testUpdatePassword() {
        //prepare
        User user = User.builder().id(2L).password("jW+C6KmMPN2LnNLUlyBFDA7cvbkvog1Z27A3Y4HEk9A=").build();
        //when
        userDao.updatePassword(user);
    }

    @Test
    @DisplayName("Find user by email")
    void testFindUserByEmail() {
        //when
        User actualUser = userDao.findByEmail("@user");
        //then
        assertNotNull(actualUser);
        assertEquals(2L, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());
    }

    @Test
    @DisplayName("Find user by email if there is no user with such email in DB")
    void testFindUserByEmailIfEmailAddressNotFound() {
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userDao.findByEmail("non-existing@email.address"));
        assertEquals("User with email: non-existing@email.address does not exist", e.getMessage());
    }

    @Test
    @DisplayName("Save access hash to the table")
    void testSaveAccessHash() {
        //prepare
        String testHash = "accessHash";
        //when
        userDao.saveAccessHash("new@new", testHash, AccessHashType.FORGOT_PASSWORD);
    }

    @Test
    @DisplayName("Check hash tables for forgot password access hash")
    @ExpectedDataSet(value = {"usersAfterChangePassword.xml", "forgot_password_hashesAfterCheckingHash.xml"})
    void testVerifyForgotPasswordAccessHash() {
        //prepare
        String newPasswordHash = "jW+C6KmMPN2LnNLUlyBFDA7cvbkvog1Z27A3Y4HEk9A=";
        User user = User.builder().id(2L).password(newPasswordHash).build();
        //when
        userDao.verifyForgotPasswordAccessHash("accessHash", user);
    }

    @Test
    @DisplayName("Search hash tables for verify email access hash and change email_verified column to 'true' in users table")
    @ExpectedDataSet(value = {"usersAfterVerifyEmail.xml", "verify_email_hashesAfterCheckingHash.xml"})
    void testVerifyEmailAccessHash() {
        //prepare
        String testHash = "accessHash";
        //when
        userDao.verifyEmailAccessHash(testHash);
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when hash is not valid")
    void testVerifyEmailAccessHashIsNotValid() {
        //prepare
        String testHash = "HashHash";
        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userDao.verifyEmailAccessHash(testHash));
        assertEquals("No user found for requested hash", e.getMessage());
    }

    @Test
    @DisplayName("Search forgot_password_hashes table for access hash and get user by ID")
    void testFindUserByForgotPasswordAccessHash() {
        //prepare
        String testHash = "accessHash";
        //when
        User user = userDao.findByForgotPasswordAccessHash(testHash);
        //then
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", user.getPassword());
        assertEquals("salt", user.getSalt());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when hash is not valid")
    void testFindUserByForgotPasswordAccessHashWhenHashIsNotValid() {
        //prepare
        String testHash = "HashHash";
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userDao.findByForgotPasswordAccessHash(testHash));
        assertEquals("No user found for requested hash", e.getMessage());
    }

    @Test
    @DisplayName("Change language")
    @ExpectedDataSet("usersAfterChangeLanguage.xml")
    void testUpdateLanguage() {
        User user = new User();
        user.setId(2);
        user.setLanguage(Language.ENGLISH);
        userDao.updateLanguage(user);
    }


    @Test
    @DisplayName("Save user from facebook")
    void saveUserFromFacebook() {
        //prepare
        User user = User.builder().facebook("facebookId").firstName("Roma").lastName("Amor").login("emailRoma")
                .email("emailRoma").salt("").password("password").language(Language.ENGLISH).build();
        //when
        userDao.saveUserFromFacebook(user);
        //then
        User actualUser = userDao.findByEmail("emailRoma");
        assertEquals(user.getFacebook(), actualUser.getFacebook());
        assertEquals(user.getFirstName(), actualUser.getFirstName());
        assertEquals(user.getLastName(), actualUser.getLastName());
        assertEquals(user.getEmail(), actualUser.getEmail());
        assertEquals(user.getSalt(), actualUser.getSalt());
    }

    @Test
    @DisplayName("Save user from Google")
    void saveUserFromGoogle() {
        //prepare
        User user = User.builder().google("googleId").firstName("Roma").lastName("Amor").login("emailRoma")
                .email("emailRoma").salt("").password("password").language(Language.ENGLISH).pathToPhoto("http").build();
        //when
        userDao.saveUserFromGoogle(user);
        //then
        User actualUser = userDao.findByEmail("emailRoma");
        assertEquals(user.getGoogle(), actualUser.getGoogle());
        assertEquals(user.getFirstName(), actualUser.getFirstName());
        assertEquals(user.getLastName(), actualUser.getLastName());
        assertEquals(user.getEmail(), actualUser.getEmail());
        assertEquals(user.getSalt(), actualUser.getSalt());
        assertEquals(user.getPathToPhoto(), actualUser.getPathToPhoto());
    }
}
