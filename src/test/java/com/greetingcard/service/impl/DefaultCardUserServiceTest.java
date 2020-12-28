package com.greetingcard.service.impl;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultCardUserServiceTest {
    @Mock
    private SecurityService userService;

    @Mock
    private CardService cardService;

    @Mock
    private CardUserDao cardUserDao;

    @InjectMocks
    private DefaultCardUserService cardUserService;

    @Test
    @DisplayName("check - admin adds user")
    void checkIfUserAdminForCard() {
        Mockito.when(cardUserDao.getUserRole(1, 1)).thenReturn(Optional.of(Role.ADMIN));
        assertDoesNotThrow(() -> cardUserService.checkIfUserAdminForCard(1, 1, "add"));
    }

    @Test
    @DisplayName("check - not admin adds user")
    void checkIfUserAdminForCardUserMember() {
        Mockito.when(cardUserDao.getUserRole(2, 1)).thenReturn(Optional.of(Role.MEMBER));
        assertThrows(IllegalArgumentException.class, () -> cardUserService.checkIfUserAdminForCard(2, 1, "add"));
    }

    @Test
    @DisplayName("check - not correct adds user")
    void checkIfUserAdminForCardWrongUser() {
        Mockito.when(cardUserDao.getUserRole(1, 3)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> cardUserService.checkIfUserAdminForCard(1, 3, "add"));
    }

    @Test
    @DisplayName("check - login is existing")
    void checkLoginExistInDBCorrectValue() {
        String login = "user_correct";
        User user = new User();
        user.setId(1);
        Mockito.when(userService.findByLogin(login)).thenReturn(user);
        long userId = cardUserService.checkLoginExistInDB(login);
        assertEquals(1, userId);
    }

    @Test
    @DisplayName("check - login is empty")
    void checkLoginForEmptyValue() {
        assertThrows(IllegalArgumentException.class, () ->
                cardUserService.checkLoginForNotEmptyValue(""));
    }

    @Test
    @DisplayName("check - login is not empty")
    void checkLoginForEmptyValueCorrect() {
        assertDoesNotThrow(() ->
                cardUserService.checkLoginForNotEmptyValue("user"));
    }

    @Test
    @DisplayName("check - card is already finished")
    void checkIfCardNotFinishedForFinished() {
        Mockito.when(cardService.getCardStatusById(2)).thenReturn(Optional.of(Status.ISOVER));
        assertThrows(IllegalArgumentException.class, () ->
                cardUserService.checkIfCardNotFinished(2));
    }

    @Test
    @DisplayName("check - card not finished")
    void checkIfCardNotFinished() {
        Mockito.when(cardService.getCardStatusById(2)).thenReturn(Optional.of(Status.ISOVER));
        assertThrows(IllegalArgumentException.class, () ->
                cardUserService.checkIfCardNotFinished(2));
    }

    @Test
    @DisplayName("check - user cannot add himself")
    void checkIfUserNotAdded() {
        Mockito.when(cardUserDao.getUserRole(1, 1)).thenReturn(Optional.of(Role.ADMIN));
        assertThrows(IllegalArgumentException.class, () -> cardUserService.checkIfUserNotAdded(1, 1));
    }

    @Test
    @DisplayName("check - user cannot add member")
    void checkIfUserNotAddedMember() {
        Mockito.when(cardUserDao.getUserRole(1, 2)).thenReturn(Optional.of(Role.MEMBER));
        assertThrows(IllegalArgumentException.class, () -> cardUserService.checkIfUserNotAdded(1, 2));
    }

    @Test
    @DisplayName("check - user adds new user")
    void checkIfUserNotAddedNewUser() {
        Mockito.when(cardUserDao.getUserRole(1, 3)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> cardUserService.checkIfUserNotAdded(1, 3));
    }
}