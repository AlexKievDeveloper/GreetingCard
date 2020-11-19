package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class JdbcUserDaoITest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private Flyway flyway;


    @BeforeEach
    void init() {
        flyway.clean();
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
        assertEquals(Language.ENGLISH, actualUser.getLanguage());
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
        //when
        userDao.update(user);
        User actualUser = userDao.findByLogin("update");
        //then
        assertEquals("update", actualUser.getFirstName());
        assertEquals("update", actualUser.getLastName());
        assertEquals("update", actualUser.getLogin());
    }
}
