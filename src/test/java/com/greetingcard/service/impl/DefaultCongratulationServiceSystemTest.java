package com.greetingcard.service.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.service.CongratulationService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@PropertySource("classpath:application.properties")
@SpringJUnitWebConfig(value = TestConfiguration.class)
class DefaultCongratulationServiceSystemTest {
    @Value("${bucketName}")
    private String bucketName;
    @Value("${region}")
    private String region;
    @Autowired
    private DefaultAmazonService defaultAmazonService;
    @Mock
    private Map<String, String> parametersMap;

    @Autowired
    @Qualifier("congratulationService")
    private CongratulationService congratulationService;
    private final byte[] bytes = new byte[1024 * 1024 * 10];


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

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat(actualList.get(2).getLink())),
                new File("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg"));

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat(actualList.get(3).getLink())),
                new File("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3"));

        assertTrue(new File("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg").exists());
        assertTrue(new File("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3").exists());

        Files.deleteIfExists(Path.of("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg"));
        Files.deleteIfExists(Path.of("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3"));

        defaultAmazonService.deleteFileFromS3Bucket(actualList.get(2).getLink());
        defaultAmazonService.deleteFileFromS3Bucket(actualList.get(3).getLink());
    }

    @Test
    @DisplayName("Updating congratulation")
    void updateCongratulationById() throws IOException {
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
        assertEquals(9, congratulation.getLinkList().size());

        assertEquals(LinkType.VIDEO, congratulation.getLinkList().get(6).getType());
        assertEquals(LinkType.PICTURE, congratulation.getLinkList().get(7).getType());
        assertEquals(LinkType.AUDIO, congratulation.getLinkList().get(8).getType());

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat(congratulation.getLinkList().get(7).getLink())),
                new File("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg"));

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat(congratulation.getLinkList().get(8).getLink())),
                new File("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3"));

        assertTrue(new File("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg").exists());
        assertTrue(new File("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3").exists());

        Files.deleteIfExists(Path.of("/home/alexander/MyWork/GreetingCard/src/test/resources/image.jpg"));
        Files.deleteIfExists(Path.of("/home/alexander/MyWork/GreetingCard/src/test/resources/audio.mp3"));

        defaultAmazonService.deleteFileFromS3Bucket(congratulation.getLinkList().get(7).getLink());
        defaultAmazonService.deleteFileFromS3Bucket(congratulation.getLinkList().get(8).getLink());
    }
}
