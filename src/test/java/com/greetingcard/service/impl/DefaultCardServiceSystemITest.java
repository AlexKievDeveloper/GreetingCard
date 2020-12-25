package com.greetingcard.service.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('congratulations_congratulation_id_seq', 6);", cleanAfter = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@PropertySource("classpath:application.properties")
public class DefaultCardServiceSystemITest {

    @Value("${bucketName}")
    private String bucketName;
    @Value("${region}")
    private String region;
    @Autowired
    private DefaultAmazonService defaultAmazonService;
    @Autowired
    private CardService cardService;
    private final byte[] bytes = new byte[1024 * 1024];

    @Test
    @DisplayName("Save background")
    void saveImageForBackground() throws IOException {
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "image/jpg", bytes);
        //when
        cardService.saveBackground(2, 2, mockImageFile);
        Card card = cardService.getCardAndCongratulationByCardId(2);
        //then
        assertNotNull(card);
        assertNotNull(card.getBackgroundImage());

        FileUtils.copyURLToFile(
                new URL("https://".concat(bucketName).concat(".s3.").concat(region).concat(".amazonaws.com")
                        .concat(card.getBackgroundImage())), new File("image.jpg"));

        assertTrue(new File("image.jpg").exists());
        Files.deleteIfExists(Path.of("image.jpg"));
        defaultAmazonService.deleteFileFromS3Bucket(card.getBackgroundImage());
    }

    @Test
    @DisplayName("Wrong format of file for background")
    void saveImageForBackground_WrongFormat() {
        //prepare
        MockMultipartFile mockImageFile = new MockMultipartFile("files_image", "image.jpg", "ups/ups", bytes);
        assertThrows(IllegalArgumentException.class, () -> cardService.saveBackground(2, 2, mockImageFile));
    }
}
