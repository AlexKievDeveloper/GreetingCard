//package com.greetingcard.dao.jdbc;
//
//import com.alibaba.fastjson.JSON;
//import com.greetingcard.entity.*;
//import org.flywaydb.core.Flyway;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import javax.sql.DataSource;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class JdbcCardDaoITest {
//    private TestConfiguration testConfiguration = new TestConfiguration();
//    private DataSource dataSource = testConfiguration.getDataSource();
//    private JdbcCardDao jdbcCardDao;
//    private Flyway flyway;
//
//    public JdbcCardDaoITest() {
//        jdbcCardDao = new JdbcCardDao(dataSource);
//        flyway = testConfiguration.getFlyway();
//    }
//
//    @BeforeEach
//    void init() {
//        flyway.migrate();
//    }
//
//    @AfterEach
//    void afterAll() {
//        flyway.clean();
//    }
//
//    @Test
//    @DisplayName("Returns List<Cards> from DB")
//    void getAllCardsByUserId() {
//        //prepare
//        Card expectedCard1 = Card.builder()
//                .id(1)
//                .name("greeting Nomar")
//                .backgroundImage(null)
//                .cardLink(null)
//                .status(Status.STARTUP)
//                .build();
//
//        Card expectedCard2 = Card.builder()
//                .id(2)
//                .name("greeting Oleksandr")
//                .backgroundImage("path_to_image")
//                .cardLink("link_to_greeting")
//                .status(Status.ISOVER)
//                .build();
//        //when
//        List<Card> actualList = jdbcCardDao.getAllCardsByUserId(1);
//        //then
//        assertTrue(actualList.contains(expectedCard1));
//        assertTrue(actualList.contains(expectedCard2));
//        assertEquals(actualList.get(0), expectedCard1);
//        assertEquals(actualList.get(1), expectedCard2);
//        for (Card card : actualList) {
//            assertNotNull(card.getUser());
//        }
//    }
//
//    @Test
//    @DisplayName("Returns List<Cards> from DB with cards where user role is Admin")
//    void getAllMyCardsByUserIdTestRoleAdmin() {
//        //prepare
//        Card expectedCard1 = Card.builder()
//                .id(1)
//                .name("greeting Nomar")
//                .backgroundImage(null)
//                .cardLink(null)
//                .status(Status.STARTUP)
//                .build();
//        //when
//        List<Card> actualList = jdbcCardDao.getCardsByUserIdAndRoleId(1, 1);
//        //then
//        assertEquals(1, actualList.size());
//        assertTrue(actualList.contains(expectedCard1));
//        assertEquals(actualList.get(0), expectedCard1);
//    }
//
//    @Test
//    @DisplayName("Returns List<Cards> from DB with cards where user role is Member")
//    void getAllMyCardsByUserIdTestRoleMember() {
//        //prepare
//        Card expectedCard2 = Card.builder()
//                .id(2)
//                .name("greeting Oleksandr")
//                .backgroundImage("path_to_image")
//                .cardLink("link_to_greeting")
//                .status(Status.ISOVER)
//                .build();
//        //when
//        List<Card> actualList = jdbcCardDao.getCardsByUserIdAndRoleId(1, 2);
//        //then
//        assertEquals(2, actualList.size());
//        assertTrue(actualList.contains(expectedCard2));
//        assertEquals(actualList.get(0), expectedCard2);
//    }
//
//    @Test
//    @DisplayName("Save new card")
//    public void createCard() {
//        //prepare
//        User user = User.builder().id(1).build();
//        Card card = Card.builder().user(user).name("greeting").status(Status.STARTUP).build();
//        //when
//        jdbcCardDao.createCard(card);
//        List<Card> actualList = jdbcCardDao.getAllCardsByUserId(1);
//        //then
//        assertEquals(4, actualList.size());
//        assertTrue(actualList.contains(card));
//        assertEquals(actualList.get(3), card);
//    }
//
//    @Test
//    @DisplayName("Return card with all congratulations")
//    public void getCardAndCongratulation() {
//        //when
//        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
//        List<Congratulation> actualCongratulationList = actualCard.getCongratulationList();
//
//        String f = JSON.toJSONString(actualCard);
//        System.out.println(f);
//        //then
//        List<Link> fromRoma = actualCongratulationList.get(0).getLinkList();
//        List<Link> fromSasha = actualCongratulationList.get(1).getLinkList();
//        List<Link> fromNastya = actualCongratulationList.get(2).getLinkList();
//
//        assertEquals(3, actualCongratulationList.size());
//        assertEquals(1, actualCard.getId());
//        assertEquals("greeting Nomar", actualCard.getName());
//        assertNull(actualCard.getBackgroundImage());
//        assertNull(actualCard.getCardLink());
//        assertEquals(Status.STARTUP, actualCard.getStatus());
//
//        assertEquals("from Roma", actualCongratulationList.get(0).getMessage());
//        assertEquals("from Sasha", actualCongratulationList.get(1).getMessage());
//        assertEquals("from Nastya", actualCongratulationList.get(2).getMessage());
//        assertEquals(1, actualCongratulationList.get(0).getUser().getId());
//        assertEquals(1, actualCongratulationList.get(1).getUser().getId());
//        assertEquals(2, actualCongratulationList.get(2).getUser().getId());
//
//        assertEquals(8, fromRoma.size());
//        assertEquals(4, fromSasha.size());
//        assertEquals(4, fromNastya.size());
//    }
//
//    @Test
//    @DisplayName("Delete card with all parameters")
//    void deleteCardById() {
//        //prepare
//        JdbcCongratulationDao congratulationDao = new JdbcCongratulationDao(dataSource);
//        //when
//        jdbcCardDao.deleteCardById(1, 1);
//        //then
//        Card actualCard = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
//        assertNull(actualCard);
//    }
//
//    @Test
//    @DisplayName("Change status of card to ISOVER")
//    void changeStatusCardById() {
//        //when
//        jdbcCardDao.changeCardStatusById(Status.ISOVER, 1);
//        //then
//        Card card = jdbcCardDao.getCardAndCongratulationByCardId(1, 1);
//        assertEquals(Status.ISOVER, card.getStatus());
//    }
//
//}
//
