package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.UserDao;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JdbcUserDaoITest {
    @Autowired
    private ApplicationContext appContext;
    private DataBaseConfigurator dataBaseConfigurator = new DataBaseConfigurator();
    private JdbcUserDao jdbcUserDao;
    private Flyway flyway;

    public JdbcUserDaoITest() {
        jdbcUserDao = new JdbcUserDao(dataBaseConfigurator.getDataSource());

        flyway = dataBaseConfigurator.getFlyway();
    }

    @BeforeEach
    void init() {
        flyway.migrate();
    }

    @AfterEach
    void afterAll() {
        flyway.clean();
    }

    @Test
    @DisplayName("Returns User from table")
    void findUserByLoginTest() {
        //prepare
        String login = "user";
        //when
        User actualUser = jdbcUserDao.findUserByLogin(login);
        //then
        assertNotNull(actualUser);
        assertEquals(2, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());

        assertEquals("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(Language.ENGLISH, actualUser.getLanguage());
    }

    @Test
    @DisplayName("Returns User from table (spring)")
    void findUserByLoginSpringTest() {
        //prepare
        String login = "user";
        //when
        jdbcUserDao = appContext.getBean("jdbcUserDao", JdbcUserDao.class);
        User actualUser = jdbcUserDao.findByLogin(login);
        //then
        assertNotNull(actualUser);
        assertEquals(2, actualUser.getId());
        assertEquals("user", actualUser.getFirstName());
        assertEquals("user", actualUser.getLastName());
        assertEquals("user", actualUser.getLogin());
        assertEquals("@user", actualUser.getEmail());

        assertEquals("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(Language.ENGLISH, actualUser.getLanguage());
    }

    @Test
    @DisplayName("Save user")
    void save() {
        //prepare
        User expected = User.builder().firstName("firstName_test").lastName("lastName_test")
                .login("login_test").email("email_test").password("password").salt("salt")
                .language(Language.ENGLISH).build();
        //when
        jdbcUserDao.save(expected);
        User actualUser = jdbcUserDao.findUserByLogin("login_test");
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
    void update() {
        //prepare
        User user = jdbcUserDao.findUserByLogin("user");
        user.setFirstName("update");
        user.setLastName("update");
        user.setLogin("update");
        //when
        jdbcUserDao.update(user);
        User actualUser = jdbcUserDao.findUserByLogin("update");
        //then
        assertEquals("update", actualUser.getFirstName());
        assertEquals("update", actualUser.getLastName());
        assertEquals("update", actualUser.getLogin());
    }

}
