package com.greetingcard.dao.jdbc;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;
import com.greetingcard.util.PropertyReader;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.ds.PGSimpleDataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JdbcUserDaoTest {
    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private PGSimpleDataSource dataSource;
    private JdbcUserDao jdbcUserDao;
    private Flyway flyway;

    JdbcUserDaoTest() {
        dataSource = new PGSimpleDataSource();
        dataSource.setURL(propertyReader.getProperty("db.url"));
        dataSource.setUser(propertyReader.getProperty("db.user"));
        dataSource.setPassword(propertyReader.getProperty("db.password"));

        flyway = Flyway.configure().dataSource(dataSource).locations("testDB/migration").load();
        jdbcUserDao = new JdbcUserDao(dataSource);
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
        assertEquals(Role.USER, actualUser.getRole());
        assertEquals("8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df", actualUser.getPassword());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(2, actualUser.getLanguage());
    }
}