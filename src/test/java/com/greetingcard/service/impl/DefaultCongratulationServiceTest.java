package com.greetingcard.service.impl;

import com.greetingcard.entity.Link;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void addYoutubeLinksTest() {
        //prepare
        String youtubeLinks = "https://www.youtube.com/watch?v=JcDy3ny-H0k\r\nhttps://www.youtube.com/watch?v=JcDy3ny-H0k";
        //when
        defaultCongratulationService.addYoutubeLinks(linkList, youtubeLinks);
        //then
        verify(linkList, times(2)).add(any());
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
    @DisplayName("Adds plain links to linksList")
    void addPlainLinksTest() {
        //prepare
        String plainLinks = "https://www.duolingo.com\r\nhttps://www.duolingo.com";
        //when
        defaultCongratulationService.addPlainLinks(linkList, plainLinks);
        //then
        verify(linkList, times(2)).add(any());
    }
}