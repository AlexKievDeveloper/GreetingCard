package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcCongratulationDaoITest {
    private DataBaseConfigurator dataBaseConfigurator = new DataBaseConfigurator();
    private JdbcCongratulationDao jdbcCongratulationDao;
    private Flyway flyway;

    public JdbcCongratulationDaoITest() {
        jdbcCongratulationDao = new JdbcCongratulationDao(dataBaseConfigurator.getDataSource());
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
    @DisplayName("Returns an object of class Congratulation from result set")
    void getCongratulationByIdTest() {
        //when
        Congratulation actualCongratulation = jdbcCongratulationDao.getCongratulationById(1);

        //then
        assertEquals(1, actualCongratulation.getId());
        assertEquals("from Roma" , actualCongratulation.getMessage());
        assertEquals(1, actualCongratulation.getCard().getId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());

        assertEquals(1, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1" , actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());

        assertEquals(2, actualCongratulation.getLinkList().get(1).getId());
        assertEquals("you_tube_2" , actualCongratulation.getLinkList().get(1).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(1).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(1).getType());

        assertEquals(4, actualCongratulation.getLinkList().get(2).getId());
        assertEquals("audio_1" , actualCongratulation.getLinkList().get(2).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(2).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(2).getType());

        assertEquals(5, actualCongratulation.getLinkList().get(3).getId());
        assertEquals("audio_2" , actualCongratulation.getLinkList().get(3).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(3).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(3).getType());

        assertEquals(7, actualCongratulation.getLinkList().get(4).getId());
        assertEquals("image_1" , actualCongratulation.getLinkList().get(4).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(4).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(4).getType());

        assertEquals(8, actualCongratulation.getLinkList().get(5).getId());
        assertEquals("image_2" , actualCongratulation.getLinkList().get(5).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(5).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(5).getType());

        assertEquals(10, actualCongratulation.getLinkList().get(6).getId());
        assertEquals("link_1" , actualCongratulation.getLinkList().get(6).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(6).getCongratulationId());
        assertEquals(LinkType.PLAIN_LINK, actualCongratulation.getLinkList().get(6).getType());

        assertEquals(11, actualCongratulation.getLinkList().get(7).getId());
        assertEquals("link_1" , actualCongratulation.getLinkList().get(7).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(7).getCongratulationId());
        assertEquals(LinkType.PLAIN_LINK, actualCongratulation.getLinkList().get(7).getType());
    }

    @Test
    @DisplayName("Saving an object of class Congratulation to the DB")
    void saveTest() {
        //prepare
        Link link = Link.builder()
                .link("you_tube_1")
                .congratulationId(7)
                .type(LinkType.VIDEO)
                .build();

        List<Link> linkList = new ArrayList<>();
        linkList.add(link);

        Congratulation congratulation = Congratulation.builder()
                .message("from JdbcTest")
                .card(Card.builder().id(2).build())
                .user(User.builder().id(2).build())
                .status(Status.STARTUP)
                .build();

        congratulation.setLinkList(linkList);

        //when
        jdbcCongratulationDao.save(congratulation);

        //then
        Congratulation actualCongratulation = jdbcCongratulationDao.getCongratulationById(7);
        assertEquals(7, actualCongratulation.getId());
        assertEquals("from JdbcTest" , actualCongratulation.getMessage());
        assertEquals(2, actualCongratulation.getCard().getId());
        assertEquals(2, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());
        assertEquals(13, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1" , actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(7, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());
    }

    @Test
    @DisplayName("Saving an object of class Congratulation to the DB")
    void saveLinksTest() throws SQLException {
        //prepare
        Connection connection = dataBaseConfigurator.getDataSource().getConnection();

        Link link = Link.builder()
                .link("you_tube_3")
                .congratulationId(6)
                .type(LinkType.VIDEO)
                .build();

        List<Link> linkList = new ArrayList<>();
        linkList.add(link);

        //when
        jdbcCongratulationDao.saveLinks(linkList, connection);

        //then
        Congratulation congratulation = jdbcCongratulationDao.getCongratulationById(6);
        Link actualLink = congratulation.getLinkList().get(0);
        assertEquals("you_tube_3" , actualLink.getLink());
        assertEquals(6, actualLink.getCongratulationId());
        assertEquals(LinkType.VIDEO, actualLink.getType());
    }

    @Test
    @DisplayName("Delete card with all parameters")
    void leaveByCardId() {
        //when
        jdbcCongratulationDao.leaveByCardId(1, 1);
        //then

        Congratulation actualCongratulation1 = jdbcCongratulationDao.getCongratulationById(1);
        Congratulation actualCongratulation2 = jdbcCongratulationDao.getCongratulationById(2);
        Congratulation actualCongratulation3 = jdbcCongratulationDao.getCongratulationById(3);

        assertNull(actualCongratulation1);
        assertNull(actualCongratulation2);
        assertNotNull(actualCongratulation3);
    }
}

