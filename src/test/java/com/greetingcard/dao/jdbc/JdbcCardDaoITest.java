package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('cards_card_id_seq', 3); SELECT setval(' users_cards_users_cards_id_seq', 6);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JdbcCardDaoITest {
    @Autowired
    private JdbcCardDao jdbcCardDao;

    @Autowired
    private Flyway flyway;

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @Test
    @DisplayName("Returns List<Cards> from DB")
    void getAllCardsByUserId() {
        //prepare
        Card expectedCard1 = Card.builder()
                .id(1)
                .name("greeting Nomar")
                .backgroundImage("path_to_image")
                .cardLink("link_to_greeting")
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
                .backgroundImage("path_to_image")
                .cardLink("link_to_greeting")
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
        assertEquals("path_to_image", actualCard.getBackgroundImage());
        assertEquals("link_to_greeting", actualCard.getCardLink());
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