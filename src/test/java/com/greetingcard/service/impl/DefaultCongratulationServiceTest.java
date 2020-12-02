package com.greetingcard.service.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = TestConfiguration.class)
class DefaultCongratulationServiceTest {
    @Mock
    private Map<String, String> parametersMap;

    @Autowired
    private DefaultCongratulationService congratulationService;
    private List<Link> linkList;
    private final byte[] bytes = new byte[1024 * 1024 * 10];

    @BeforeEach
    void setUp() {
        linkList = new ArrayList<>();
    }

    @Test
    @DisplayName("Adds links to linkList")
    void getLinkListTest() throws IOException {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        MultipartFile[] mockAudioFiles = new MultipartFile[]{mockAudioFile};
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        when(parametersMap.get("youtube")).thenReturn(youtubeLinks);

        //when
        List<Link> actualList = congratulationService.getLinkList(mockImageFiles, mockAudioFiles, parametersMap);

        //then
        verify(parametersMap).get("youtube");
        assertNotNull(actualList);
        assertEquals(4, actualList.size());
        assertEquals("JcDy3ny-H0k", actualList.get(0).getLink());
        assertEquals("JcDy3ny-H0k", actualList.get(1).getLink());
        assertEquals(LinkType.PICTURE, actualList.get(2).getType());
        assertEquals(LinkType.AUDIO, actualList.get(3).getType());
        assertTrue(new File("/greeting-cards".concat(actualList.get(2).getLink())).exists());
        assertTrue(new File("/greeting-cards".concat(actualList.get(3).getLink())).exists());
        Files.deleteIfExists(Path.of("/greeting-cards".concat(actualList.get(2).getLink())));
        Files.deleteIfExists(Path.of("/greeting-cards".concat(actualList.get(3).getLink())));
    }

    @Test
    @DisplayName("Adds youtube links to linkList")
    void addYoutubeLinksTest() {
        //prepare
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when
        congratulationService.addYoutubeLinks(linkList, youtubeLinks);
        //then
        assertEquals(2, linkList.size());
        assertEquals("JcDy3ny-H0k", linkList.get(0).getLink());
        assertEquals("JcDy3ny-H0k", linkList.get(1).getLink());
        assertEquals(LinkType.VIDEO, linkList.get(0).getType());
        assertEquals(LinkType.VIDEO, linkList.get(1).getType());
    }

    @Test
    @DisplayName("Throws illegal argument exception if youtube url is incorrect")
    void addYoutubeLinksTestException() {
        //prepare
        String youtubeLinks = "https://www.yoube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.addYoutubeLinks(linkList, youtubeLinks));
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Returns youtube video id from youtube url")
    void getYoutubeVideoIdTest() {
        //prepare
        String expectedYoutubeVideoId = "JcDy3ny-H0k";
        //when
        String actualYoutubeVideoId = congratulationService.getYoutubeVideoId("https://www.youtube.com/watch?v=JcDy3ny-H0k");
        //then
        assertEquals(expectedYoutubeVideoId, actualYoutubeVideoId);
    }

    @Test
    @DisplayName("Throws illegal argument exception if youtube url has incorrect format")
    void getYoutubeVideoIdExceptionTest() {
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.getYoutubeVideoId("https://www.youtube.com"));
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Return List of links(String format) from text")
    void getYoutubeLinksListFromText() {
        //prepare
        String text = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com\r\n";
        //when
        List<String> actualLinkList = congratulationService.getYoutubeLinksListFromText(text);
        //then
        assertNotNull(actualLinkList);
        assertEquals(2, actualLinkList.size());
        assertEquals("https://www.youtube.com/watch?v=JcDy3ny-H0k", actualLinkList.get(0));
        assertEquals("https://www.youtube.com", actualLinkList.get(1));
    }

    @Test
    @DisplayName("Throw exception if link is not match valid youtube link format")
    void getYoutubeLinksListFromTextThrowExceptionIfNotValidFormat() {
        //prepare
        String text = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com\r\nhttps://www.studytonight.com/servlet/httpsession.php#";
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.getYoutubeLinksListFromText(text));
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Throw exception if link is not match valid youtube link format")
    void getYoutubeLinksListFromTextThrowExceptionIfLinkIsTooLong() {
        //prepare
        String text = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.commmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" +
                "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm";
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.getYoutubeLinksListFromText(text));
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksImageCase() throws IOException {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);

        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        //when
        congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);
        //then
        assertEquals(1, linkList.size());
        assertEquals(LinkType.PICTURE, linkList.get(0).getType());
        assertThat(linkList.get(0).getLink(), matchesPattern("(^\\Wimg\\W)(\\S*)(\\Sjpg$)"));
        assertTrue(new File("/greeting-cards".concat(linkList.get(0).getLink())).exists());
        Files.deleteIfExists(Path.of("/greeting-cards".concat(linkList.get(0).getLink())));
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksAudioCase() throws IOException {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_audio", "audio.mpeg", "audio/mpeg", bytes);

        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        //when
        congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);
        //then
        assertEquals(1, linkList.size());
        assertEquals(LinkType.AUDIO, linkList.get(0).getType());
        assertTrue(new File("/greeting-cards".concat(linkList.get(0).getLink())).exists());
        Files.deleteIfExists(Path.of("/greeting-cards".concat(linkList.get(0).getLink())));
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksThrowExceptionIfContentTypeNoeValid() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/gif", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};

        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList));
        assertEquals("Sorry, this format is not supported by the application: image/gif", e.getMessage());
    }


    @Test
    @DisplayName("Updating congratulation")
    void updateCongratulationById() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        MultipartFile[] mockAudioFiles = new MultipartFile[]{mockAudioFile};
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("message", "Congratulation from updateCongratulationById test");
        parametersMap.put("youtube", "https://www.youtube.com/watch?v=BmBr5diz8WA");

        //when
        congratulationService.updateCongratulationById(mockImageFiles, mockAudioFiles, parametersMap, 1, 1);

        //then
        Congratulation congratulation = congratulationService.getCongratulationById(1);
        assertEquals("Congratulation from updateCongratulationById test", congratulation.getMessage());
    }
}
