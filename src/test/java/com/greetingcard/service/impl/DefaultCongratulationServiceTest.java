package com.greetingcard.service.impl;

import com.greetingcard.entity.Link;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultCongratulationServiceTest {
    @Mock
    private List<Link> linkList;
    @InjectMocks
    private DefaultCongratulationService defaultCongratulationService;

    @Test
    @DisplayName("Adds youtube links to linkList")
    void getLinkListTest() {
        //prepare
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        String plainLinks = "https://www.duolingo.com\r\nhttps://www.duolingo.com";
        //when
        List<Link> actualList = defaultCongratulationService.getLinkList(youtubeLinks, plainLinks);
        //then
        assertNotNull(actualList);
        assertEquals(4, actualList.size());
        assertEquals("JcDy3ny-H0k", actualList.get(0).getLink());
        assertEquals("JcDy3ny-H0k", actualList.get(1).getLink());
        assertEquals("https://www.duolingo.com", actualList.get(2).getLink());
        assertEquals("https://www.duolingo.com", actualList.get(3).getLink());
    }

    @Test
    @DisplayName("Adds youtube links to linkList")
    void addYoutubeLinksTest() {
        //prepare
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when
        defaultCongratulationService.addYoutubeLinks(linkList, youtubeLinks);
        //then
        verify(linkList, times(2)).add(any());
    }

    @Test
    @DisplayName("Throws illegal argument exception if youtube url is incorrect")
    void addYoutubeLinksTestException() {
        //prepare
        String youtubeLinks = "https://www.yoube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when + then
        var e = assertThrows(IllegalArgumentException.class, () ->
                defaultCongratulationService.addYoutubeLinks(linkList, youtubeLinks));
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
        var e = assertThrows(IllegalArgumentException.class, () ->
                defaultCongratulationService.getYoutubeVideoId("https://www.youtube.com"));
        assertEquals("Wrong youtube link url!", e.getMessage());
    }

    @Test
    @DisplayName("Adds plain links to linksList")
    void addPlainLinksTest() {
        //prepare
        String plainLinks = "https://www.duolingo.com\r\nhttps://www.duolingo.com";
        //when
        defaultCongratulationService.addPlainLinks(linkList, plainLinks);
        //then
        verify(linkList, times(2)).add(any());
    }

    @Test
    @DisplayName("Do not create link if plainLinks field is empty")
    void addPlainLinksTestEmptyField() {
        //prepare
        String plainLinks = "";
        //when
        defaultCongratulationService.addPlainLinks(linkList, plainLinks);
        //then
        verify(linkList, times(0)).add(any());
        assertEquals(0, linkList.size());
    }

    @Test
    @DisplayName("Throws illegal argument exception if link is too long")
    void addPlainLinksTestLinkLengthIsToLong() {
        //prepare
        String plainLinks = "https://www.studytonight.com/servlet/httpsession.phppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp" +
                "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp";
        //when
        var e = assertThrows(IllegalArgumentException.class, () ->
                defaultCongratulationService.addPlainLinks(linkList, plainLinks));
        //then
        verify(linkList, times(0)).add(any());
        assertEquals(0, linkList.size());
        assertEquals("Sorry, congratulation not saved. The link is very long. " +
                "Please use a link up to 500 characters.", e.getMessage());
    }
}