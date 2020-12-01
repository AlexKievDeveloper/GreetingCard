package com.greetingcard.service.impl;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CongratulationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Setter
public class DefaultCongratulationService implements CongratulationService {
    private CongratulationDao congratulationDao;
    private String rootDirectory;

    public DefaultCongratulationService(CongratulationDao congratulationDao, String rootDirectory) {
        this.congratulationDao = congratulationDao;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return congratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public List<Link> getLinkList(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String, String> parametersMap) {
        List<Link> linkList = new ArrayList<>();
        addYoutubeLinks(linkList, parametersMap.get("youtube"));
        log.info("Service level.Created youtube links");
        addLinksToLocalImagesAndAudioFiles(files_image, files_audio, linkList);
        log.info("Service level.Saved image and audio files and created links");
        return linkList;
    }

    @Override
    public void save(Congratulation congratulation) {
        congratulationDao.save(congratulation);
    }

    @Override
    public void changeCongratulationStatusByCongratulationId(Status status, long congratulationId) {
        congratulationDao.changeCongratulationStatusByCongratulationId(status, congratulationId);
    }

    @Override
    public void deleteById(long congratulationId, long userId) {
        congratulationDao.deleteById(congratulationId, userId);
    }

    @Override
    public void deleteByCardId(long cardId, long userId) {
        congratulationDao.deleteByCardId(cardId, userId);
    }

    void addYoutubeLinks(List<Link> linkList, String youtubeLinks) {

        List<String> youtubeLinksCollection = getYoutubeLinksListFromText(youtubeLinks);

        for (String youtubeLink : youtubeLinksCollection) {
            Link video = Link.builder()
                    .link(getYoutubeVideoId(youtubeLink))
                    .type(LinkType.VIDEO)
                    .build();
            linkList.add(video);
        }
    }

    String getYoutubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?" +
                "feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else throw new IllegalArgumentException("Wrong youtube link url!");
    }

    void addLinksToLocalImagesAndAudioFiles(MultipartFile[] files_image, MultipartFile[] files_audio, List<Link> linkList) {
        saveFilesAndCreateLinks(files_image, linkList);
        saveFilesAndCreateLinks(files_audio, linkList);
    }

    List<String> getYoutubeLinksListFromText(String text) {
        String pattern = "(https?://)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(text);

        List<String> linkList = new ArrayList<>();

        while (matcher.find()) {
            String youtubeLink = matcher.group();
            if (!youtubeLink.contains("youtu") || youtubeLink.length() > 500) {
                throw new IllegalArgumentException("Wrong youtube link url!");
            }
            linkList.add(youtubeLink);
        }
        return linkList;
    }

    void saveFilesAndCreateLinks(MultipartFile[] files, List<Link> linkList) {
        if (files != null) {
            for (MultipartFile multipartFile : files) {
                log.info("saveFilesAndCreateLinks. Multipart: {}", multipartFile.getOriginalFilename());
                if (multipartFile.getSize() > 1000 && !multipartFile.isEmpty()) {
                    String salt = UUID.randomUUID().toString();
                    String fileName = multipartFile.getOriginalFilename();
                    String linkContentType = multipartFile.getContentType();

                    if (linkContentType != null && fileName != null) {
                        String uniqueFileName = salt.concat(fileName);
                        LinkType linkType = null;
                        String pathToStorage = null;

                        for (LinkType linkTypeValue : LinkType.values()) {
                            if (linkTypeValue.getAdditionalTypes().contains(linkContentType)) {
                                linkType = linkTypeValue;
                                pathToStorage = linkTypeValue.getPathToStorage();
                            }
                        }

                        if (linkType == null) {
                            throw new IllegalArgumentException("Sorry, this format is not supported by the application: ".concat(linkContentType));
                        }

                        Link link = Link.builder()
                                .link("/".concat(pathToStorage).concat("/").concat(uniqueFileName))
                                .type(linkType)
                                .build();
                        log.info("saveFilesAndCreateLinks.I`m going to add link to list: {}", link.getLink());
                        linkList.add(link);

                        try {
                            Files.createDirectories(Paths.get(rootDirectory, pathToStorage));
                            log.info("Root directory: {}, path to storage: {}, uniqueFileName: {}", rootDirectory, pathToStorage, uniqueFileName);
                            multipartFile.transferTo(Paths.get(rootDirectory, pathToStorage, uniqueFileName));
                        } catch (IOException e) {
                            log.error("Exception while saving multipart file: {}", multipartFile, e);
                            throw new RuntimeException("Exception while saving multipart file: " + multipartFile);
                        }
                    }
                }
            }
        }
    }
}
