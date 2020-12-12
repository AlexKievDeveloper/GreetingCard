package com.greetingcard.security;

import com.greetingcard.dao.UserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceTest {
    @InjectMocks
    private DefaultSecurityService mockSecurityService;
    @Mock
    private UserDao mockUserDao;

    @Test
    @DisplayName("Returns hashed password")
    void getHashPasswordTest() {
        //prepare
        String salt = "salt";
        String testPassword = "user";
        String expectedPassword = "gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=";

        DefaultSecurityService defaultSecurityService = new DefaultSecurityService();
        defaultSecurityService.setAlgorithm("SHA-256");
        defaultSecurityService.setIteration(1);
        //when
        String actualPassword = defaultSecurityService.getHashPassword(salt.concat(testPassword));
        //then
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    @DisplayName("Find user by id")
    void findById() {
        //when
        mockSecurityService.findById(1);
        //then
        verify(mockUserDao).findById(1);
    }

    @Test
    @DisplayName("Find user by login")
    void testFindByLogin() {
        //when
        mockSecurityService.findByLogin("login");
        //then
        verify(mockUserDao).findByLogin("login");
    }
}
