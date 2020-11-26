package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = FlywayConfig.class)
class DefaultCongratulationServiceTest {
    @Mock
    private Map<String, String> parametersMap;

    @Autowired
    private DefaultCongratulationService defaultCongratulationService;
    private List<Link> linkList;

    @BeforeEach
    void setUp() {
        linkList = new ArrayList<>();
    }

    @Test
    @DisplayName("Adds links to linkList")
    void getLinkListTest() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", "test-image.jpg".getBytes());
        MockMultipartFile mockAudioFile = new MockMultipartFile("files_audio", "audio.mp3", "audio/mpeg", "test-audio.mp3".getBytes());
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        MultipartFile[] mockAudioFiles = new MultipartFile[]{mockAudioFile};
        String imageLinks = "https://www.davno.ru/assets/images/cards/big/birthday-1061.jpg\r\nhttps://www.davno.ru/assets/images/cards/big/birthday-1061.jpg";
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        when(parametersMap.get("image_links")).thenReturn(imageLinks);
        when(parametersMap.get("youtube")).thenReturn(youtubeLinks);

        //when
        List<Link> actualList = defaultCongratulationService.getLinkList(mockImageFiles, mockAudioFiles, parametersMap);

        //then
        verify(parametersMap).get("image_links");
        verify(parametersMap).get("youtube");
        assertNotNull(actualList);
    }

/*    @Test
    @DisplayName("Adds links to linkList")
    void addImageLinksTest() {
        //prepare
        String image_links = "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi \r\n" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi";
        //when
        defaultCongratulationService.addImageLinks(linkList, image_links);
        //then
        assertEquals(2, linkList.size());
        assertEquals("https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi", linkList.get(0).getLink());
        assertEquals("https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi", linkList.get(1).getLink());
        assertEquals(LinkType.PICTURE, linkList.get(0).getType());
        assertEquals(LinkType.PICTURE, linkList.get(1).getType());
    }

    @Test
    @DisplayName("Adds links to linkList")
    void addImageLinksTestThrowException() {
        //prepare
        String image_links = "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi";
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            defaultCongratulationService.addImageLinks(linkList, image_links);
        });
        assertEquals("Sorry, congratulation not saved. The link is very long. Please use a link up to 500 characters.", e.getMessage());
    }
*/
    @Test
    @DisplayName("Adds youtube links to linkList")
    void addYoutubeLinksTest() {
        //prepare
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when
        defaultCongratulationService.addYoutubeLinks(linkList, youtubeLinks);
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
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            defaultCongratulationService.addYoutubeLinks(linkList, youtubeLinks);
        });
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Returns youtube video id from youtube url")
    void getYoutubeVideoIdTest() {
        //prepare
        String expectedYoutubeVideoId = "JcDy3ny-H0k";
        //when
        String actualYoutubeVideoId = defaultCongratulationService.getYoutubeVideoId("https://www.youtube.com/watch?v=JcDy3ny-H0k");
        //then
        assertEquals(expectedYoutubeVideoId, actualYoutubeVideoId);
    }

    @Test
    @DisplayName("Throws illegal argument exception if youtube url has incorrect format")
    void getYoutubeVideoIdExceptionTest() {
        //when + then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            defaultCongratulationService.getYoutubeVideoId("https://www.youtube.com");
        });
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Return List of links(String format) from text")
    void getLinksListFromText() {
        //prepare
        String text = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com\r\n" +
                "https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi";
        //when
        List<String> actualLinkList = defaultCongratulationService.getLinksListFromText(text);
        //then
        assertNotNull(actualLinkList);
        assertEquals(3, actualLinkList.size());
        assertEquals("https://www.youtube.com/watch?v=JcDy3ny-H0k", actualLinkList.get(0));
        assertEquals("https://www.youtube.com", actualLinkList.get(1));
        assertEquals("https://lh3.googleusercontent.com/proxy/wUdjgP5yX8ThxJ2JhAf7k-hWzj_U8sZJi1-q_Z66vD55gSzYNlsXs_PdQ9RCTwS0inYy4IDV-03WLhiqAnXAmI0131mtEz-tie49C0pDiNRaPFq0fC8w-y10oOMctTBUOnySAKfAZmcmBUeDpfoi", actualLinkList.get(2));
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksImageCase() throws IOException {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg",
                ("test-imageeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee").getBytes());

        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        //when
        defaultCongratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);
        //then
        assertEquals(1, linkList.size());
        assertEquals(LinkType.PICTURE, linkList.get(0).getType());
        Files.deleteIfExists(Path.of(linkList.get(0).getLink()));
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksAudioCase() throws IOException {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_audio", "audio.mpeg", "audio/mpeg",
                ("test-audiooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo").getBytes());

        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};
        //when
        defaultCongratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);
        //then
        assertEquals(1, linkList.size());
        assertEquals(LinkType.AUDIO, linkList.get(0).getType());
        Files.deleteIfExists(Path.of(linkList.get(0).getLink()));
    }
}
