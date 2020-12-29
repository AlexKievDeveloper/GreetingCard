package com.greetingcard.web.security.jwt;

import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void generateToken() {
        String token = jwtProvider.generateToken("user");

        assertTrue(jwtProvider.validateToken(token));
        assertEquals("user", jwtProvider.getLoginFromToken(token));
    }

    @Test
    void validateToken() {
        assertFalse(jwtProvider.validateToken("1hlkj"));
    }
}