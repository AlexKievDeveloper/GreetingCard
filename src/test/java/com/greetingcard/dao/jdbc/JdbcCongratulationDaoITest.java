package com.greetingcard.dao.jdbc;

import com.greetingcard.entity.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class JdbcCongratulationDaoITest {
    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcCongratulationDao jdbcCongratulationDao;

    @BeforeEach
    void init() {
        flyway.clean();
        flyway.migrate();
    }

    @AfterEach
    void afterAll() throws IOException {
        flyway.clean();
        Files.deleteIfExists(Path.of("src/main/webapp/static/audio"));
        Files.deleteIfExists(Path.of("src/main/webapp/static/img"));
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
        assertEquals(1, actualCongratulation.getCardId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());

        assertEquals(1, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("iywaBOMvYLI", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());

        assertEquals(2, actualCongratulation.getLinkList().get(1).getId());
        assertEquals("L-iepu3EtyE", actualCongratulation.getLinkList().get(1).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(1).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(1).getType());

        assertEquals(4, actualCongratulation.getLinkList().get(2).getId());
        assertEquals("https://i.postimg.cc/kXRG5yRC/images.jpg", actualCongratulation.getLinkList().get(2).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(2).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(2).getType());

        assertEquals(5, actualCongratulation.getLinkList().get(3).getId());
        assertEquals("https://i.postimg.cc/hvfjTLC9/images-1.jpg", actualCongratulation.getLinkList().get(3).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(3).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(3).getType());

        assertEquals(7, actualCongratulation.getLinkList().get(4).getId());
        assertEquals("https://www.dropbox.com/s/8cg7h5gehrk7joy/dzidzo_-_kolomijka_bojkivska_%28zf.fm%29.mp3?dl=0", actualCongratulation.getLinkList().get(4).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(4).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(4).getType());

        assertEquals(8, actualCongratulation.getLinkList().get(5).getId());
        assertEquals("https://www.dropbox.com/s/3u94pftverackzy/kolomijki_-_kolomijka_zastilna_%28zf.fm%29.mp3?dl=0", actualCongratulation.getLinkList().get(5).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(5).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(5).getType());

        assertEquals(10, actualCongratulation.getLinkList().get(6).getId());
        assertEquals("https://www.dropbox.com/s/o7i5as1axjmg9if/kolomijki_-_oj__marichko__chicheri_%28zv.fm%29.mp3?dl=0", actualCongratulation.getLinkList().get(6).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(6).getCongratulationId());
        assertEquals(LinkType.PLAIN_LINK, actualCongratulation.getLinkList().get(6).getType());

        assertEquals(11, actualCongratulation.getLinkList().get(7).getId());
        assertEquals("https://www.youtube.com/watch?v=YlUKcNNmywk", actualCongratulation.getLinkList().get(7).getLink());
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
                .cardId(2L)
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
        assertEquals(2, actualCongratulation.getCardId());
        assertEquals(2, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());
        assertEquals(17, actualCongratulation.getLinkList().get(0).getId());
        assertEquals("you_tube_1", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(7, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());
    }

    @Test
    @DisplayName("Saving an object of class Congratulation to the DB")
    void saveTestExceptionToLongLinkValue() {
        //prepare
        Link link = Link.builder()
                .link("https://www.studytonight.com/servlet/httpsession.phppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp")
                .congratulationId(7)
                .type(LinkType.PLAIN_LINK)
                .build();

        List<Link> linkList = new ArrayList<>();
        linkList.add(link);

        Congratulation congratulation = Congratulation.builder()
                .message("from JdbcTest")
                .cardId(2L)
                .user(User.builder().id(2).build())
                .status(Status.STARTUP)
                .build();

        congratulation.setLinkList(linkList);

        //when + then
        assertThrows(DataIntegrityViolationException.class, () -> jdbcCongratulationDao.save(congratulation));
    }

    @Test
    @DisplayName("Delete congratulations by id of card with all parameters")
    void deleteByCardId() throws IOException {
        //prepare
        Files.createDirectories(Path.of("src/main/webapp/static"));
        Files.createFile(Path.of("src/main/webapp/static/audio"));
        Files.createFile(Path.of("src/main/webapp/static/img"));
        List<Link> links = new ArrayList<>();
        links.add(Link.builder().link("src/main/webapp/static/audio").type(LinkType.AUDIO).build());
        links.add(Link.builder().link("src/main/webapp/static/img").type(LinkType.PICTURE).build());

        Congratulation congratulation = Congratulation.builder()
                .cardId(1L)
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
        assertFalse(Files.exists(Path.of("src/main/webapp/static/img")));
    }

    @Test
    @DisplayName("Delete congratulations by id with all parameters")
    void deleteById() throws IOException {
        //prepare
        Files.createDirectories(Path.of("src/main/webapp/static"));
        Files.createFile(Path.of("src/main/webapp/static/audio"));
        Files.createFile(Path.of("src/main/webapp/static/img"));
        List<Link> links = new ArrayList<>();
        links.add(Link.builder().link("src/main/webapp/static/audio").congratulationId(7).type(LinkType.AUDIO).build());
        links.add(Link.builder().link("src/main/webapp/static/img").congratulationId(7).type(LinkType.PICTURE).build());

        Congratulation congratulation = Congratulation.builder()
                .cardId(2)
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
        assertFalse(Files.exists(Path.of("src/main/webapp/static/img")));
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
        assertEquals(4, actualCongratulation3.getLinkList().size());

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

    @Test
    @DisplayName("Change status to STARTUP congratulations by id of card")
    void changeCongratulationStatusByCongratulationId() {
        //prepare
        Congratulation congratulationBefore = jdbcCongratulationDao.getCongratulationById(1);
        assertEquals(Status.STARTUP, congratulationBefore.getStatus());

        //when
        jdbcCongratulationDao.changeCongratulationStatusByCongratulationId(Status.ISOVER, 1);

        //then
        Congratulation congratulationAfter = jdbcCongratulationDao.getCongratulationById(1);
        assertEquals(Status.ISOVER, congratulationAfter.getStatus());
    }
}

