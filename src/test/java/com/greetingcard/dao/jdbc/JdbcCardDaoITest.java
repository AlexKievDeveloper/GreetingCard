package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(value = FlywayConfig.class)
public class JdbcCardDaoITest {
    @Autowired
    private JdbcCardDao jdbcCardDao;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void init() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("Returns List<Cards> from DB")
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
        List<Card> actualList = jdbcCardDao.getAllCardsByUserId(1);
        //then
        assertTrue(actualList.contains(expectedCard1));
        assertTrue(actualList.contains(expectedCard2));
        assertEquals(actualList.get(0), expectedCard1);
        assertEquals(actualList.get(1), expectedCard2);
        for (Card card : actualList) {
            assertNotNull(card.getUser());
        }
    }

    @Test
    @DisplayName("Returns List<Cards> from DB with cards where user role is Admin")
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
        List<Card> actualList = jdbcCardDao.getCardsByUserIdAndRoleId(1, 1);
        //then
        assertEquals(1, actualList.size());
        assertTrue(actualList.contains(expectedCard1));
        assertEquals(actualList.get(0), expectedCard1);
    }

    @Test
    @DisplayName("Returns List<Cards> from DB with cards where user role is Member")
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
        List<Card> actualList = jdbcCardDao.getCardsByUserIdAndRoleId(1, 2);
        //then
        assertEquals(2, actualList.size());
        assertTrue(actualList.contains(expectedCard2));
        assertEquals(actualList.get(0), expectedCard2);
    }

    @Test
    @DisplayName("Save new card")
    public void createCard() {
        //prepare
        User user = User.builder().id(1).build();
        Card card = Card.builder().user(user).name("greeting").status(Status.STARTUP).build();
        //when
        Long id = jdbcCardDao.createCard(card);
        List<Card> actualList = jdbcCardDao.getAllCardsByUserId(1);
        Card actualCard = actualList.get(3);
        //then
        assertEquals(4, id);
        assertEquals(4, actualList.size());
        assertEquals(actualCard.getUser().getId(), card.getUser().getId());
        assertEquals(actualCard.getName(), card.getName());
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
        assertEquals("greeting Nomar", actualCard.getName());
        assertNull(actualCard.getBackgroundImage());
        assertNull(actualCard.getCardLink());
        assertEquals(Status.STARTUP, actualCard.getStatus());

        assertEquals("from Roma", actualCongratulationList.get(0).getMessage());
        assertEquals("from Sasha", actualCongratulationList.get(1).getMessage());
        assertEquals("from Nastya", actualCongratulationList.get(2).getMessage());
        assertEquals(1, actualCongratulationList.get(0).getUser().getId());
        assertEquals(1, actualCongratulationList.get(1).getUser().getId());
        assertEquals(2, actualCongratulationList.get(2).getUser().getId());

        assertEquals(8, fromRoma.size());
        assertEquals(4, fromSasha.size());
        assertEquals(4, fromNastya.size());
    }

    @Test
    @DisplayName("Return null when user does not has cards or access")
    public void getCardAndCongratulationNoAccess() {
        //when
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, -1);
        //then
        assertNull(actualCard);
    }

    @Test
    @DisplayName("Return null if card does not exist")
    public void getCardAndCongratulationNotExist() {
        //when
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(-1000, 1);
        //then
        assertNull(actualCard);
    }

    @Test
    @DisplayName("Delete card with all parameters")
    void deleteCardById() {
        //when
        jdbcCardDao.deleteCardById(1, 1);
        //then
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
        assertNull(actualCard);
    }

    @Test
    @DisplayName("Remove card if user does not have access")
    void deleteCardByIdNoAccess() {
        //when
        jdbcCardDao.deleteCardById(1, 10000);
        //then
        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
        assertNotNull(actualCard);
    }

    @Test
    @DisplayName("Change status of card to ISOVER")
    void changeStatusCardById() {
        //when
        jdbcCardDao.changeCardStatusById(Status.ISOVER, 1);
        //then
        Card card = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
        assertEquals(Status.ISOVER, card.getStatus());
    }

}