package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JdbcCardDaoITest {
    private DataBaseConfigurator dataBaseConfigurator = new DataBaseConfigurator();
    private JdbcCardDao jdbcCardDao;
    private Flyway flyway;

    public JdbcCardDaoITest() {
        jdbcCardDao = new JdbcCardDao(dataBaseConfigurator.getDataSource());
        flyway = dataBaseConfigurator.getFlyway();
    }

    @BeforeEach
    void init() {
        flyway.migrate();
    }

    @AfterEach
    void afterAll() {
        flyway.clean();
    }

    @Test
    @DisplayName("Returns Map<Cards, Role> from DB")
    void getAllCardsByUserId() {
        //prepare
        Card expectedCard1 = Card.builder()
                .id(1)
                .name("greeting Nomar")
                .backgroundImage(null)
                .cardLink(null)
                .status(Status.STARTUP)
                .build();

        Card expectedCard2 = Card.builder()
                .id(2)
                .name("greeting Oleksandr")
                .backgroundImage("path_to_image")
                .cardLink("link_to_greeting")
                .status(Status.ISOVER)
                .build();
        //when
        Map<Card, Role> actualMap = jdbcCardDao.getAllCardsByUserId(1);
        //then
        assertTrue(actualMap.containsKey(expectedCard1));
        assertTrue(actualMap.containsKey(expectedCard2));
        assertEquals(actualMap.get(expectedCard1), Role.ADMIN);
        assertEquals(actualMap.get(expectedCard2), Role.MEMBER);
    }

    @Test
    @DisplayName("Save new card")
    public void createCard(){
        //prepare
        Card card = Card.builder().name("greeting").status(Status.STARTUP).build();
        User user = User.builder().id(1).build();
        //when
        jdbcCardDao.createCard(card,user);
        Map<Card, Role> actualMap = jdbcCardDao.getAllCardsByUserId(1);
        //then
        assertEquals(3,actualMap.size());
        assertTrue(actualMap.containsKey(card));
        assertEquals(actualMap.get(card), Role.ADMIN);
    }
}

