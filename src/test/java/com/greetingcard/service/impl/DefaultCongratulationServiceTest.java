package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultCongratulationServiceTest {
    @InjectMocks
    private DefaultCongratulationService congratulationService;
    private List<Link> linkList;
    private final byte[] bytes = new byte[1024 * 1024 * 10];

    @BeforeEach
    void setUp() {
        linkList = new ArrayList<>();
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
    void saveFilesAndCreateLinksThrowExceptionIfContentTypeNoValid() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/gif", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};

        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList));
        assertEquals("Sorry, this format is not supported by the application: image/gif", e.getMessage());
    }
}
