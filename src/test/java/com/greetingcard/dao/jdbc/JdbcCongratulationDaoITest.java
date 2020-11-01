package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcCongratulationDaoITest {
    private DataBaseConfigurator dataBaseConfigurator = new DataBaseConfigurator();
    private DataSource dataSource = dataBaseConfigurator.getDataSource();
    private JdbcCongratulationDao jdbcCongratulationDao;
    private Flyway flyway;

    public JdbcCongratulationDaoITest() {
        jdbcCongratulationDao = new JdbcCongratulationDao(dataSource);
        flyway = dataBaseConfigurator.getFlyway();
    }

    @BeforeEach
    void init() {
        flyway.migrate();
    }

    @AfterEach
    void afterAll() throws IOException {
        flyway.clean();
        Files.deleteIfExists(Path.of("src/main/webapp/static/audio"));
        Files.deleteIfExists(Path.of("src/main/webapp/static/picture"));
        Files.deleteIfExists(Path.of("src/main/webapp/static"));
    }

    @Test
    @DisplayName("Returns an object of class Congratulation from result set")
    void getCongratulationByIdTest() {
        //when
        Congratulation actualCongratulation = jdbcCongratulationDao.getCongratulationById(1);

        //then
        assertEquals(1, actualCongratulation.getId());
        assertEquals("from Roma", actualCongratulation.getMessage());
        assertEquals(1, actualCongratulation.getCard().getId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());

        assertEquals(1, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());

        assertEquals(2, actualCongratulation.getLinkList().get(1).getId());
        assertEquals("you_tube_2", actualCongratulation.getLinkList().get(1).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(1).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(1).getType());

        assertEquals(4, actualCongratulation.getLinkList().get(2).getId());
        assertEquals("audio_1", actualCongratulation.getLinkList().get(2).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(2).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(2).getType());

        assertEquals(5, actualCongratulation.getLinkList().get(3).getId());
        assertEquals("audio_2", actualCongratulation.getLinkList().get(3).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(3).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(3).getType());

        assertEquals(7, actualCongratulation.getLinkList().get(4).getId());
        assertEquals("image_1", actualCongratulation.getLinkList().get(4).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(4).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(4).getType());

        assertEquals(8, actualCongratulation.getLinkList().get(5).getId());
        assertEquals("image_2", actualCongratulation.getLinkList().get(5).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(5).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(5).getType());

        assertEquals(10, actualCongratulation.getLinkList().get(6).getId());
        assertEquals("link_1", actualCongratulation.getLinkList().get(6).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(6).getCongratulationId());
        assertEquals(LinkType.PLAIN_LINK, actualCongratulation.getLinkList().get(6).getType());

        assertEquals(11, actualCongratulation.getLinkList().get(7).getId());
        assertEquals("link_1", actualCongratulation.getLinkList().get(7).getLink());
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
        assertEquals("from JdbcTest", actualCongratulation.getMessage());
        assertEquals(2, actualCongratulation.getCard().getId());
        assertEquals(2, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());
        assertEquals(13, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1", actualCongratulation.getLinkList().get(0).getLink());
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
        assertEquals("you_tube_3", actualLink.getLink());
        assertEquals(6, actualLink.getCongratulationId());
        assertEquals(LinkType.VIDEO, actualLink.getType());
    }

    @Test
    @DisplayName("Delete congratulations by id of card with all parameters")
    void deleteByCardId() throws IOException {
        //prepare
        Files.createDirectories(Path.of("src/main/webapp/static"));
        Files.createFile(Path.of("src/main/webapp/static/audio"));
        Files.createFile(Path.of("src/main/webapp/static/picture"));
        List<Link> links = new ArrayList<>();
        links.add(Link.builder().link("audio").type(LinkType.AUDIO).build());
        links.add(Link.builder().link("picture").type(LinkType.PICTURE).build());

        Congratulation congratulation = Congratulation.builder()
                .card(Card.builder().id(1).build())
                .user(User.builder().id(1).build())
                .message("test delete link")
                .status(Status.STARTUP)
                .linkList(links)
                .build();
        jdbcCongratulationDao.save(congratulation);

        //when
        jdbcCongratulationDao.deleteByCardId(1, 1);

        //then
        List<Congratulation> congratulationList = jdbcCongratulationDao.findCongratulationsByCardId(1);
        assertEquals(1, congratulationList.size());
        assertEquals(3, congratulationList.get(0).getId());
        assertFalse(Files.exists(Path.of("src/main/webapp/static/audio")));
        assertFalse(Files.exists(Path.of("src/main/webapp/static/picture")));
    }

    @Test
    @DisplayName("Delete congratulations by id with all parameters")
    void deleteById() throws IOException {
        //prepare
        Files.createDirectories(Path.of("src/main/webapp/static"));
        Files.createFile(Path.of("src/main/webapp/static/audio"));
        Files.createFile(Path.of("src/main/webapp/static/picture"));
        List<Link> links = new ArrayList<>();
        links.add(Link.builder().link("audio").congratulationId(7).type(LinkType.AUDIO).build());
        links.add(Link.builder().link("picture").congratulationId(7).type(LinkType.PICTURE).build());

        Congratulation congratulation = Congratulation.builder()
                .card(Card.builder().id(1).build())
                .user(User.builder().id(1).build())
                .message("test delete link")
                .status(Status.STARTUP)
                .linkList(links)
                .build();
        jdbcCongratulationDao.save(congratulation);

        //when
        jdbcCongratulationDao.deleteById(7, 1);

        //then
        Congratulation actual = jdbcCongratulationDao.getCongratulationById(7);
        assertNull(actual);
        assertFalse(Files.exists(Path.of("src/main/webapp/static/audio")));
        assertFalse(Files.exists(Path.of("src/main/webapp/static/picture")));
    }

    @Test
    @DisplayName("Find all congratulations by id of card")
    void findCongratulationsByCardId() {
        //when
        List<Congratulation> congratulationList = jdbcCongratulationDao.findCongratulationsByCardId(1);
        Congratulation actualCongratulation1 = congratulationList.get(0);
        Congratulation actualCongratulation2 = congratulationList.get(1);
        Congratulation actualCongratulation3 = congratulationList.get(2);

        //then
        assertEquals(3, congratulationList.size());
        assertEquals(1, actualCongratulation1.getId());
        assertEquals(2, actualCongratulation2.getId());
        assertEquals(3, actualCongratulation3.getId());

        assertEquals(8, actualCongratulation1.getLinkList().size());
        assertEquals(4, actualCongratulation2.getLinkList().size());
        assertEquals(0, actualCongratulation3.getLinkList().size());

        assertEquals(1, actualCongratulation1.getUser().getId());
        assertEquals(1, actualCongratulation2.getUser().getId());
        assertEquals(2, actualCongratulation3.getUser().getId());
    }

    @Test
    @DisplayName("Change status to ISOVER congratulations by id of card")
    void changeStatusCongratulationsByCardIdToIsOver() {
        //when
        jdbcCongratulationDao.changeStatusCongratulationsByCardId(Status.ISOVER, 1);
        List<Congratulation> congratulationList = jdbcCongratulationDao.findCongratulationsByCardId(1);
        Congratulation actualCongratulation1 = congratulationList.get(0);
        Congratulation actualCongratulation2 = congratulationList.get(1);
        Congratulation actualCongratulation3 = congratulationList.get(2);

        //then
        assertEquals(Status.ISOVER, actualCongratulation1.getStatus());
        assertEquals(Status.ISOVER, actualCongratulation2.getStatus());
        assertEquals(Status.ISOVER, actualCongratulation3.getStatus());
    }

    @Test
    @DisplayName("Change status to STARTUP congratulations by id of card")
    void changeStatusCongratulationsByCardIdToStartUp() {
        //when
        jdbcCongratulationDao.changeStatusCongratulationsByCardId(Status.STARTUP, 1);

        List<Congratulation> congratulationList = jdbcCongratulationDao.findCongratulationsByCardId(1);
        Congratulation actualCongratulation1 = congratulationList.get(0);
        Congratulation actualCongratulation2 = congratulationList.get(1);
        Congratulation actualCongratulation3 = congratulationList.get(2);

        //then
        assertEquals(Status.STARTUP, actualCongratulation1.getStatus());
        assertEquals(Status.STARTUP, actualCongratulation2.getStatus());
        assertEquals(Status.STARTUP, actualCongratulation3.getStatus());
    }
}

