package com.greetingcard.security;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.UserDao;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DBRider
@DBUnit(caseSensitiveTableNames = false, caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml",  "types.xml", "roles.xml",  "statuses.xml", "users.xml",  "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 3);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
@ExtendWith(MockitoExtension.class)
class DefaultSecurityServiceTest {
    @Autowired
    private DefaultSecurityService securityService;

    @InjectMocks
    private DefaultSecurityService mockSecurityService;

    @Mock
    private UserDao userDao;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp(){
        flyway.migrate();
    }

    @Test
    @DisplayName("Login user")
    void loginTest() {
        //when
        User actual = securityService.login("user", "user");

        //then
        assertEquals("user", actual.getLogin());
        assertEquals("user", actual.getFirstName());
        assertEquals("user", actual.getLastName());
        assertEquals("@user", actual.getEmail());
        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", actual.getPassword());
        assertEquals("salt", actual.getSalt());
        assertEquals(Language.UKRAINIAN, actual.getLanguage());
    }

    @Test
    @DisplayName("Login user if login too long")
    void testLoginIfLoginTooLong() {
        assertThrows(IllegalArgumentException.class, () ->
                securityService.login("logintoooooooooooooooooooooloooooooooooooooooooooonggggg", "user"));
    }

    @Test
    @DisplayName("Returns hashed password")
    void getHashPasswordTest() {
        //prepare
        String salt = "salt";
        String testPassword = "user";
        String expectedPassword = "gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=";
        //when
        String actualPassword = securityService.getHashPassword(salt.concat(testPassword));
        //then
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    @DisplayName("Find user by id")
    void findById(){
        //when
        mockSecurityService.findById(1);
        //then
        verify(userDao).findById(1);
    }

}
