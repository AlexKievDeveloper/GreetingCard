package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JdbcCardDaoITest {
    private DataBaseConfigurator dataBaseConfigurator = new DataBaseConfigurator();
    private DataSource dataSource = dataBaseConfigurator.getDataSource();
    private JdbcCardDao jdbcCardDao;
    private Flyway flyway;

    public JdbcCardDaoITest() {
        jdbcCardDao = new JdbcCardDao(dataSource);
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
        for (Card card : actualMap.keySet()) {
            assertNotNull(card.getUser());
        }

    }

    @Test
    @DisplayName("Returns Map<Cards, Role> from DB with cards where user role is Admin")
    void getAllMyCardsByUserIdTestRoleAdmin() {
        //prepare
        Card expectedCard1 = Card.builder()
                .id(1)
                .name("greeting Nomar")
                .backgroundImage(null)
                .cardLink(null)
                .status(Status.STARTUP)
                .build();
        //when
        Map<Card, Role> actualMap = jdbcCardDao.getCardsByUserIdAndRoleId(1, 1);
        //then
        assertEquals(1, actualMap.size());
        assertTrue(actualMap.containsKey(expectedCard1));
        assertEquals(actualMap.get(expectedCard1), Role.ADMIN);
    }

    @Test
    @DisplayName("Returns Map<Cards, Role> from DB with cards where user role is Member")
    void getAllMyCardsByUserIdTestRoleMember() {
        //prepare
        Card expectedCard2 = Card.builder()
                .id(2)
                .name("greeting Oleksandr")
                .backgroundImage("path_to_image")
                .cardLink("link_to_greeting")
                .status(Status.ISOVER)
                .build();
        //when
        Map<Card, Role> actualMap = jdbcCardDao.getCardsByUserIdAndRoleId(1, 2);
        //then
        assertEquals(2, actualMap.size());
        assertTrue(actualMap.containsKey(expectedCard2));
        assertEquals(actualMap.get(expectedCard2), Role.MEMBER);
    }

    @Test
    @DisplayName("Save new card")
    public void createCard() {
        //prepare
        User user = User.builder().id(1).build();
        Card card = Card.builder().user(user).name("greeting").status(Status.STARTUP).build();
        //when
        jdbcCardDao.createCard(card);
        Map<Card, Role> actualMap = jdbcCardDao.getAllCardsByUserId(1);
        List<Card> cardList = new ArrayList<>(actualMap.keySet());
        //then
        assertEquals(4, actualMap.size());
        assertTrue(actualMap.containsKey(card));
        assertEquals(actualMap.get(card), Role.ADMIN);
    }

    @Test
    @DisplayName("Return card with all congratulations")
    public void getCardAndCongratulation() {
        //when
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
        List<Congratulation> actualCongratulationList = actualCard.getCongratulationList();

        //then
        List<Link> fromRoma = actualCongratulationList.get(0).getLinkList();
        List<Link> fromSasha = actualCongratulationList.get(1).getLinkList();
        List<Link> fromNastya = actualCongratulationList.get(2).getLinkList();

        assertEquals(3, actualCongratulationList.size());
        assertEquals(1, actualCard.getId());
        assertEquals("greeting Nomar" , actualCard.getName());
        assertNull(actualCard.getBackgroundImage());
        assertNull(actualCard.getCardLink());
        assertEquals(Status.STARTUP, actualCard.getStatus());

        assertEquals("from Roma" , actualCongratulationList.get(0).getMessage());
        assertEquals("from Sasha" , actualCongratulationList.get(1).getMessage());
        assertEquals("from Nastya" , actualCongratulationList.get(2).getMessage());
        assertEquals(1, actualCongratulationList.get(0).getUser().getId());
        assertEquals(1, actualCongratulationList.get(1).getUser().getId());
        assertEquals(2, actualCongratulationList.get(2).getUser().getId());

        assertEquals(8, fromRoma.size());
        assertEquals(4, fromSasha.size());
        assertEquals(0, fromNastya.size());
    }

    @Test
    @DisplayName("Delete card with all parameters")
    void deleteCardById() throws IOException {
        //prepare
        JdbcCongratulationDao congratulationDao = new JdbcCongratulationDao(dataSource);
        //when
        jdbcCardDao.deleteCardById(1, 1);
        //then
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
        Congratulation actualCongratulation1 = congratulationDao.getCongratulationById(1);
        Congratulation actualCongratulation2 = congratulationDao.getCongratulationById(2);
        Congratulation actualCongratulation3 = congratulationDao.getCongratulationById(3);

        assertNull(actualCard);
        assertNull(actualCongratulation1);
        assertNull(actualCongratulation2);
        assertNull(actualCongratulation3);
    }

}

