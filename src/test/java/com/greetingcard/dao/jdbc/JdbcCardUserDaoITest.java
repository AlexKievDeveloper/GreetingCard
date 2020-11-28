package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.entity.Role;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringJUnitWebConfig(value = TestConfiguration.class)
@DBRider
@DataSet(value = {"languages.xml",  "types.xml", "roles.xml",  "statuses.xml", "users.xml",  "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
       cleanAfter = true)
class JdbcCardUserDaoITest {

    @Autowired
    private JdbcCardUserDao jdbcCardUserDao;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.migrate();
    }

    @Test
    @ExpectedDataSet(value = "cardUsersAdded.xml")
    void addMember() {
        jdbcCardUserDao.addUserMember(1, 3);
    }

    @Test
    void getUserRoleAdmin() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 1);

        assertTrue(role.isPresent());
        assertEquals(role.get().getRoleNumber(), 1);
    }

    @Test
    void getUserRoleMember() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 2);

        assertTrue(role.isPresent());
        assertEquals(role.get().getRoleNumber(), 2);
    }

    @Test
    void getUserRoleNoRecord() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 3);
        assertTrue(role.isEmpty());
    }
}