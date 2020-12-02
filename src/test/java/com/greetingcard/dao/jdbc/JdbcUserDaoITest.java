package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.UserDao;
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
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
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

}
