package com.greetingcard.service.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.CardUserDao;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.User;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.service.CardUserService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval(' users_cards_users_cards_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultCardUserServiceMockITest {
    @Autowired
    private CardUserService cardUserService;

    @MockBean
    private CardUserDao cardUserDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @Test
    @ExpectedDataSet(value = {"cardUser/cardUsers.xml", "congratulations.xml"})
    void deleteUsersWithError() {
        List<UserInfo> userInfoList = new ArrayList<>(2);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfoList.add(userInfo);
        UserInfo userInfo4 = new UserInfo();
        userInfo4.setId(4);
        userInfoList.add(userInfo4);
        User userLoggedIn = new User();
        userLoggedIn.setId(2);

        doThrow(new RuntimeException("user can't be deleted")).when(cardUserDao).deleteListUsers(2, userInfoList);
        assertThrows(RuntimeException.class, () -> cardUserService.deleteUsers(2, userInfoList, userLoggedIn));
    }

    @Test
    @ExpectedDataSet(value = {"cardUser/cardUsers.xml", "congratulations.xml"})
    void deleteUserWithError() {
        doThrow(new RuntimeException("user can't be deleted")).when(cardUserDao).deleteUserFromCard(1, 1);
        assertThrows(RuntimeException.class, () -> cardUserService.deleteUserFromCard(1, 1));
    }
}
