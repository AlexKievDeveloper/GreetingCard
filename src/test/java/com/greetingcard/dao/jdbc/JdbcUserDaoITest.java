package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.AccessHashType;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml", "forgot_password_hashes.xml", "verify_email_hashes.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class,  RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    @Autowired
    private UserDao userDao;

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
    @DisplayName("Find user by login if login didn't create")
    void testFindUserByLoginIfLoginNotFound() {
        //when
        User actualUser = userDao.findByLogin("user_is_not_created");
        //then
        assertNull(actualUser);
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
    @DisplayName("Find user by id")
    void testFindUserById() {
        //when
        User actualUser = userDao.findById(2);
        //then
        assertNotNull(actualUser);
        assertEquals(2, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());
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
        //when
        User actualUser = userDao.findByEmail("non-existing@email.address");
        //then
        assertNull(actualUser);
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
}
