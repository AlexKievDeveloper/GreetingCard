package com.greetingcard.security;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class DefaultSecurityServiceTest {

    @Autowired
    private DefaultSecurityService securityService;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void init() {
        flyway.clean();
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
        assertEquals(Language.ENGLISH, actual.getLanguage());
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
}