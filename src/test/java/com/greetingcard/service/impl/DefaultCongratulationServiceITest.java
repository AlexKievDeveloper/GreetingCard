package com.greetingcard.service.impl;


import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = {TestConfiguration.class,  RootApplicationContext.class})
public class DefaultCongratulationServiceITest {
    @Mock
    private DefaultAmazonService defaultAmazonService;
    @InjectMocks
    private DefaultCongratulationService congratulationService;

    private List<Link> linkList;
    private final byte[] bytes = new byte[1024 * 1024 * 10];

    @BeforeEach
    void setUp() {
        linkList = new ArrayList<>();
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksImageCase() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};

        //when
        congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);

        //then
        verify(defaultAmazonService).uploadFile(any(), anyString());
        assertEquals(1, linkList.size());
        assertEquals(LinkType.PICTURE, linkList.get(0).getType());
        assertThat(linkList.get(0).getLink(), matchesPattern("(^\\Wimg\\W)(\\S*)(\\Sjpg$)"));
    }

    @Test
    @DisplayName("Saving files and create link(path to file) for each file")
    void saveFilesAndCreateLinksAudioCase() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_audio", "audio.mpeg", "audio/mpeg", bytes);
        MultipartFile[] mockImageFiles = new MultipartFile[]{mockImageFile};

        //when
        congratulationService.saveFilesAndCreateLinks(mockImageFiles, linkList);

        //then
        verify(defaultAmazonService).uploadFile(any(), anyString());
        assertEquals(1, linkList.size());
        assertEquals(LinkType.AUDIO, linkList.get(0).getType());
        assertThat(linkList.get(0).getLink(), matchesPattern("(^\\Waudio\\W)(\\S*)(\\Smpeg$)"));
    }
}
