package com.greetingcard.service.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
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
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.ArrayList;
import java.util.List;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval(' users_cards_users_cards_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DefaultCardUserServiceITest {

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private CardUserDao cardUserDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @Test
    @ExpectedDataSet(value = {"cardUser/cardUsersListDeleted.xml", "cardUser/congratulationsDeleted.xml"})
    void deleteUsers() {
        List<UserInfo> userInfoList = new ArrayList<>(2);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfoList.add(userInfo);
        UserInfo userInfo4 = new UserInfo();
        userInfo4.setId(4);
        userInfoList.add(userInfo4);

        User userDeletes = new User();
        userDeletes.setId(2);

        cardUserService.deleteUsers(2, userInfoList, userDeletes);
    }

}
