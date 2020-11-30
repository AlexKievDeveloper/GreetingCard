package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval(' users_cards_users_cards_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcCardUserDaoITest {

    @Autowired
    private JdbcCardUserDao jdbcCardUserDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @Test
    @ExpectedDataSet("cardUser/cardUsersAdded.xml")
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

    @Test
    void getUserMembersByCardIdEmptyList() {
        List<UserInfo> userInfoList = jdbcCardUserDao.getUserMembersByCardId(3);
        assertEquals(0, userInfoList.size());
    }

    @Test
    void getUserMembersByCardIdSomeUsers() {
        List<UserInfo> userInfoList = jdbcCardUserDao.getUserMembersByCardId(2);
        assertEquals(2, userInfoList.size());
        assertEquals(1, userInfoList.get(0).getId());
        assertEquals(4, userInfoList.get(1).getId());
    }

    @Test
    @ExpectedDataSet("cardUser/cardUsersDeleted.xml")
    void deleteUserFromCard() {
        jdbcCardUserDao.deleteUserFromCard(1, 2);
    }

    @Test
    void getNamesOfParams() {
        String[] arrayOfParameters = {"user_id0", "user_id1"};
        String parameters = jdbcCardUserDao.getNamesOfParams(arrayOfParameters);

        assertEquals("(:user_id0,:user_id1)", parameters);
    }


    @Test
    @ExpectedDataSet("cardUser/cardUsersListDeleted.xml")
    void deleteListUsers() {
        List<UserInfo> userInfoList = new ArrayList<>(2);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfoList.add(userInfo);
        UserInfo userInfo4 = new UserInfo();
        userInfo4.setId(4);
        userInfoList.add(userInfo4);

        jdbcCardUserDao.deleteListUsers(2, userInfoList);
    }
}