package com.greetingcard.service.impl;

import com.greetingcard.dao.file.LocalDiskFileDao;
import com.greetingcard.entity.Link;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCongratulationServiceTest {
    @Mock
    private Part part;
    @Mock
    private HttpServletRequest request;
    @Mock
    private List<Link> linkList;
    @Mock
    private LocalDiskFileDao localDiskFileDao;
    @InjectMocks
    private DefaultCongratulationService defaultCongratulationService;

    @Test
    @DisplayName("Returns list with links")
    void getLinkListTest() {
        //prepare
        List<Part> parts = new ArrayList<>();
        parts.add(part);
        when(part.getContentType()).thenReturn("image/jpeg");
        when(part.getSubmittedFileName()).thenReturn("name");
        when(request.getParameter("youtube")).thenReturn("https://www.youtube.com/watch?v=JcDy3ny-H0k");
        when(request.getParameter("plain-link")).thenReturn("https://www.duolingo.com");
        //when
        defaultCongratulationService.getLinkList(parts, request);
        //then
        verify(request).getParameter("youtube");
        verify(request).getParameter("plain-link");
        verify(part).getContentType();
        verify(part).getSubmittedFileName();
        verify(localDiskFileDao).saveFileInStorage(any(), any());
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
    @DisplayName("Adds links to images and audio file to linkList and saving files in DB")
    void addLinksToImagesAndAudioFilesTest() {
        //prepare
        List<Part> parts = new ArrayList<>();
        parts.add(part);
        when(part.getContentType()).thenReturn("image/jpeg");
        when(part.getSubmittedFileName()).thenReturn("name");
        //when
        defaultCongratulationService.addLinksToImagesAndAudioFiles(parts, linkList);
        //then
        verify(part).getContentType();
        verify(part).getSubmittedFileName();
        verify(localDiskFileDao).saveFileInStorage(any(), any());
        verify(linkList).add(any());
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