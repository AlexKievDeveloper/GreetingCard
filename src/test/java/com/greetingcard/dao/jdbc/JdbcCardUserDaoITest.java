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
@SpringJUnitWebConfig(value = FlywayConfig.class)
@DBRider
@DataSet(cleanBefore = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcCardUserDaoITest {

    @Autowired
    private JdbcCardUserDao jdbcCardUserDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void createDB() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DataSet(value = "cardUsers.xml")
    @ExpectedDataSet(value = "cardUsersAdded.xml")
    void addMember() {
        jdbcCardUserDao.addUserMember(1, 3);
    }

    @Test
    @DataSet(value = "cardUsers.xml")
    void getUserRoleAdmin() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 1);

        assertTrue(role.isPresent());
        assertEquals(role.get().getRoleNumber(), 1);
    }

    @Test
    @DataSet(value = "cardUsers.xml")
    void getUserRoleMember() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 2);

        assertTrue(role.isPresent());
        assertEquals(role.get().getRoleNumber(), 2);
    }

    @Test
    @DataSet(value = "cardUsers.xml")
    void getUserRoleNoRecord() {
        Optional<Role> role = jdbcCardUserDao.getUserRole(1, 3);
        assertTrue(role.isEmpty());
    }

    @AfterAll
    public void cleanUp() {
        flyway.clean();
    }
}