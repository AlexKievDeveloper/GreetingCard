package com.greetingcard.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.entity.*;
import com.greetingcard.service.impl.DefaultAmazonService;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('links_link_id_seq', 1); SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcCongratulationDaoITest {
    @Autowired
    private DefaultAmazonService defaultAmazonService;

    @Autowired
    private JdbcCongratulationDao congratulationDao;

    @Autowired
    private Flyway flyway;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${region}")
    private String region;

    private final byte[] bytes = new byte[1024 * 1024 * 10];

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @AfterEach
    void clear() throws IOException {
        Files.deleteIfExists(Path.of("img1.jpg"));
        Files.deleteIfExists(Path.of("img2.jpg"));
        Files.deleteIfExists(Path.of("img3.jpg"));
        Files.deleteIfExists(Path.of("audio1.mp3"));
        Files.deleteIfExists(Path.of("audio2.mp3"));
        Files.deleteIfExists(Path.of("audio3.mp3"));
    }

    @Test
    @DisplayName("Returns an object of class Congratulation from result set")
    void getCongratulationByIdTest() {
        //when
        Optional<Congratulation> optionalCongratulation = congratulationDao.getCongratulationById(1);
        Congratulation actualCongratulation = optionalCongratulation.get();

        //then
        assertTrue(optionalCongratulation.isPresent());

        assertEquals(1, actualCongratulation.getId());
        assertEquals("from Roma", actualCongratulation.getMessage());
        assertEquals(1, actualCongratulation.getCardId());
        assertEquals(1, actualCongratulation.getUser().getId());
        assertEquals(Status.STARTUP, actualCongratulation.getStatus());

        assertEquals(6, actualCongratulation.getLinkList().size());

        assertEquals("iywaBOMvYLI", actualCongratulation.getLinkList().get(0).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(0).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(0).getType());

        assertEquals("L-iepu3EtyE", actualCongratulation.getLinkList().get(1).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(1).getCongratulationId());
        assertEquals(LinkType.VIDEO, actualCongratulation.getLinkList().get(1).getType());

        assertEquals("/audio/audio1.mp3", actualCongratulation.getLinkList().get(2).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(2).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(2).getType());

        assertEquals("/audio/audio2.mp3", actualCongratulation.getLinkList().get(3).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(3).getCongratulationId());
        assertEquals(LinkType.AUDIO, actualCongratulation.getLinkList().get(3).getType());

        assertEquals("/img/img1.jpg",
                actualCongratulation.getLinkList().get(4).getLink());
        assertEquals("/img/img1.jpg", actualCongratulation.getLinkList().get(4).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(4).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(4).getType());

        assertEquals("/img/img2.jpg", actualCongratulation.getLinkList().get(5).getLink());
        assertEquals(1, actualCongratulation.getLinkList().get(5).getCongratulationId());
        assertEquals(LinkType.PICTURE, actualCongratulation.getLinkList().get(5).getType());
    }

    @Test
    @DisplayName("Returns an object of class Congratulation from result set")
    void getCongratulationById_IfNotExistTest() {
        //when
        Optional<Congratulation> optionalCongratulation = congratulationDao.getCongratulationById(1000);

        //then
        assertFalse(optionalCongratulation.isPresent());
    }

    @Test
    @ExpectedDataSet(value = {"save_congratulation.xml"})
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
        congratulationDao.save(congratulation);
    }

    @Test
    @DisplayName("Saving an object of class Congratulation to the DB")
    void saveTestExceptionToLongLinkValue() {
        //prepare
        Link link = Link.builder()
                .link("https://www.youtube.com/watch?v=k7PlG32BzI8pppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                        "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp")
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

        //when + then
        assertThrows(DataIntegrityViolationException.class, () -> congratulationDao.save(congratulation));
    }

    @Test
    @ExpectedDataSet(value = {"congratulationsAfterDeleteByCardId.xml", "linksAfterDeleteByCardId.xml"})
    @DisplayName("Delete congratulations by id of card with all parameters")
    void deleteByCardId() throws IOException {
        //prepare
        MockMultipartFile mockImageFile1 = new MockMultipartFile("files_image", "img1.jpg", "image/jpg", bytes);
        MockMultipartFile mockImageFile2 = new MockMultipartFile("files_image", "img2.jpg", "image/jpg", bytes);
        MockMultipartFile mockImageFile3 = new MockMultipartFile("files_image", "img3.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile1 = new MockMultipartFile("files_audio", "audio1.mp3", "audio/mpeg", bytes);
        MockMultipartFile mockAudioFile2 = new MockMultipartFile("files_audio", "audio2.mp3", "audio/mpeg", bytes);
        MockMultipartFile mockAudioFile3 = new MockMultipartFile("files_audio", "audio3.mp3", "audio/mpeg", bytes);

        defaultAmazonService.uploadFile(mockImageFile1, "img/img1.jpg");
        defaultAmazonService.uploadFile(mockImageFile2, "img/img2.jpg");
        defaultAmazonService.uploadFile(mockImageFile3, "img/img3.jpg");
        defaultAmazonService.uploadFile(mockAudioFile1, "audio/audio1.mp3");
        defaultAmazonService.uploadFile(mockAudioFile2, "audio/audio2.mp3");
        defaultAmazonService.uploadFile(mockAudioFile3, "audio/audio3.mp3");

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img1.jpg")),
                new File("img1.jpg"));
        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img2.jpg")),
                new File("img2.jpg"));
        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img3.jpg")),
                new File("img3.jpg"));
        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio1.mp3")),
                new File("audio1.mp3"));
        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio2.mp3")),
                new File("audio2.mp3"));
        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio3.mp3")),
                new File("audio3.mp3"));

        assertTrue(new File("img1.jpg").exists());
        assertTrue(new File("img2.jpg").exists());
        assertTrue(new File("img3.jpg").exists());
        assertTrue(new File("audio1.mp3").exists());
        assertTrue(new File("audio2.mp3").exists());
        assertTrue(new File("audio3.mp3").exists());

        //when
        congratulationDao.deleteByCardId(1, 1);

        //then
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img1.jpg")),
                new File("img1.jpg")));
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img2.jpg")),
                new File("img2.jpg")));
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img3.jpg")),
                new File("img3.jpg")));
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio1.mp3")),
                new File("audio1.mp3")));
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio2.mp3")),
                new File("audio2.mp3")));
        assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio3.mp3")),
                new File("audio3.mp3")));
    }

    @Test
    @DisplayName("Delete congratulations by id with all parameters")
    void deleteById() {
        //prepare
        List<Link> links = new ArrayList<>();
        links.add(Link.builder().link("/audio/audio1.mp3").congratulationId(7).type(LinkType.AUDIO).build());
        links.add(Link.builder().link("/img/img1.jpg").congratulationId(7).type(LinkType.PICTURE).build());

        Congratulation congratulation = Congratulation.builder()
                .cardId(2)
                .user(User.builder().id(1).build())
                .message("test delete link")
                .status(Status.STARTUP)
                .linkList(links)
                .build();
        congratulationDao.save(congratulation);

        //when
        congratulationDao.deleteById(7, 1);

        //then
        Optional<Congratulation> actual = congratulationDao.getCongratulationById(7);
        assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("Find all congratulations by id of card")
    void findCongratulationsByCardId() {
        //when
        List<Congratulation> congratulationList = congratulationDao.findCongratulationsByCardId(1);

        Congratulation actualCongratulation1 = congratulationList.get(0);
        Congratulation actualCongratulation2 = congratulationList.get(1);
        Congratulation actualCongratulation3 = congratulationList.get(2);

        //then
        assertEquals(3, congratulationList.size());
        assertEquals(1, actualCongratulation1.getId());
        assertEquals(2, actualCongratulation2.getId());
        assertEquals(3, actualCongratulation3.getId());

        assertEquals(6, actualCongratulation1.getLinkList().size());
        assertEquals(3, actualCongratulation2.getLinkList().size());
        assertEquals(0, actualCongratulation3.getLinkList().size());

        assertEquals(1, actualCongratulation1.getUser().getId());
        assertEquals(1, actualCongratulation2.getUser().getId());
        assertEquals(2, actualCongratulation3.getUser().getId());
    }

    @Test
    @DisplayName("Change status to ISOVER congratulations by id of card")
    void changeStatusCongratulationsByCardIdToIsOver() {
        //when
        congratulationDao.changeCongratulationsStatusByCardId(Status.ISOVER, 1);
        List<Congratulation> congratulationList = congratulationDao.findCongratulationsByCardId(1);
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
        congratulationDao.changeCongratulationsStatusByCardId(Status.STARTUP, 1);

        List<Congratulation> congratulationList = congratulationDao.findCongratulationsByCardId(1);
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
        Optional<Congratulation> optionalCongratulation = congratulationDao.getCongratulationById(1);
        assertEquals(Status.STARTUP, optionalCongratulation.get().getStatus());

        //when
        congratulationDao.changeCongratulationStatusByCongratulationId(Status.ISOVER, 1);

        //then
        Optional<Congratulation> optionalCongratulationAfter = congratulationDao.getCongratulationById(1);
        assertEquals(Status.ISOVER, optionalCongratulationAfter.get().getStatus());
    }

    @Test
    @DisplayName("Update congratulation message by congratulation id and user id")
    void updateCongratulation() {
        //when
        congratulationDao.updateCongratulationMessage("Congratulations from updateCongratulationTest", 1, 1);

        //then
        Optional<Congratulation> optionalCongratulationAfter = congratulationDao.getCongratulationById(1);
        assertEquals("Congratulations from updateCongratulationTest", optionalCongratulationAfter.get().getMessage());
    }

    @Test
    @DisplayName("Saving links congratulation message by congratulation id and user id")
    void saveLinks() {
        //prepare
        Link link = Link.builder()
                .link("you_tube_10")
                .congratulationId(1)
                .type(LinkType.VIDEO)
                .build();

        List<Link> linkList = new ArrayList<>();
        linkList.add(link);

        //when
        congratulationDao.saveLinks(linkList, 1);

        //then
        Optional<Congratulation> optionalCongratulation = congratulationDao.getCongratulationById(1);
        Congratulation congratulation = optionalCongratulation.get();

        assertEquals(1, congratulation.getLinkList().get(6).getCongratulationId());
        assertEquals("you_tube_10", congratulation.getLinkList().get(6).getLink());
        assertEquals(LinkType.VIDEO, congratulation.getLinkList().get(6).getType());
    }

    @Test
    @DisplayName("Deleting files by links ids")
    void deleteFilesFromLinks() throws IOException {
        //prepare
        MockMultipartFile mockImageFile1 = new MockMultipartFile("files_image", "img1.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile1 = new MockMultipartFile("files_audio", "audio1.mp3", "audio/mpeg", bytes);

        defaultAmazonService.uploadFile(mockImageFile1, "img/img1.jpg");
        defaultAmazonService.uploadFile(mockAudioFile1, "audio/audio1.mp3");

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img1.jpg")),
                new File("img1.jpg"));

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio1.mp3")),
                new File("audio1.mp3"));

        assertTrue(new File("img1.jpg").exists());
        assertTrue(new File("audio1.mp3").exists());

        Link link = Link.builder()
                .id(5)
                .link("/audio/audio1.mp3")
                .build();

        Link link2 = Link.builder()
                .id(8)
                .link("/img/img1.jpg")
                .build();

        List<Link> linkList = List.of(link, link2);

        //when
        congratulationDao.deleteFilesFromLinks(linkList, 1);

        //then
        Assertions.assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/img/img1.jpg")),
                new File("img1.jpg")));
        Assertions.assertThrows(FileNotFoundException.class, () -> FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat("/audio/audio1.mp3")),
                new File("audio1.mp3")));
    }

    @Test
    @DisplayName("Deleting links by ids")
    void deleteLinksByIds() {
        //prepare
        Link link = Link.builder()
                .id(2)
                .build();

        Link link2 = Link.builder()
                .id(3)
                .build();

        List<Link> linkList = List.of(link, link2);
        Optional<Congratulation> beforeCongratulation = congratulationDao.getCongratulationById(1);
        assertEquals(6, beforeCongratulation.get().getLinkList().size());

        //when
        congratulationDao.deleteLinksById(linkList, 1);

        //then
        Optional<Congratulation> afterCongratulation = congratulationDao.getCongratulationById(1);
        assertEquals(4, afterCongratulation.get().getLinkList().size());

        for (Link linkAfter : afterCongratulation.get().getLinkList()) {
            assertNotEquals(linkAfter.getId(), 2);
            assertNotEquals(linkAfter.getId(), 3);
        }
    }

    @Test
    @DisplayName("Returns parameter names as string")
    void getLinksList() {
        //prepare
        Link link = Link.builder()
                .id(8)
                .build();

        Link link2 = Link.builder()
                .id(9)
                .build();

        List<Link> linkList = List.of(link, link2);

        //when
        List<Link> actualLinksList = congratulationDao.getLinksList(linkList, 1);

        //then
        assertNotNull(actualLinksList);
        assertEquals(2, actualLinksList.size());
        assertEquals(8, actualLinksList.get(0).getId());
        assertEquals("/img/img1.jpg", actualLinksList.get(0).getLink());
        assertEquals(LinkType.PICTURE, actualLinksList.get(0).getType());
        assertEquals(1, actualLinksList.get(1).getCongratulationId());

        assertEquals(9, actualLinksList.get(1).getId());
        assertEquals("/img/img2.jpg", actualLinksList.get(1).getLink());
        assertEquals(LinkType.PICTURE, actualLinksList.get(1).getType());
        assertEquals(1, actualLinksList.get(1).getCongratulationId());
    }


    @Test
    @DisplayName("Returns parameter names as string")
    void getNamesOfParams() {
        //prepare
        String[] namesArray = new String[]{"link_id0", "link_id1"};

        //when
        String namesRow = congratulationDao.getNamesOfParams(namesArray);

        //then
        assertEquals("(:link_id0,:link_id1)", namesRow);
    }

    @Test
    @DisplayName("Returns MapSqlParameterSource")
    void getMapSqlParameterSourceForList() {
        //prepare
        Link link = Link.builder().id(1).build();
        Link link2 = Link.builder().id(2).build();
        List<Link> linkList = List.of(link, link2);

        //when
        MapSqlParameterSource parameterSource = congratulationDao.getMapSqlParameterSourceForList(linkList);

        //then
        assertNotNull(parameterSource);
        assertEquals(2, parameterSource.getValues().size());
        assertEquals(1L, parameterSource.getValue("link_id0"));
        assertEquals(2L, parameterSource.getValue("link_id1"));
    }
}

