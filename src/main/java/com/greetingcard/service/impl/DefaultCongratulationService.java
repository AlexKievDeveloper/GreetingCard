package com.greetingcard.service.impl;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CongratulationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultCongratulationService implements CongratulationService {
    private CongratulationDao congratulationDao;
    private DefaultAmazonService defaultAmazonService;

    @Override
    public Congratulation getCongratulationById(long congratulationId) {
        return congratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public List<Link> getLinkList(MultipartFile[] files_image, MultipartFile[] files_audio,
                                  Map<String, String> parametersMap) {
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
    public void changeCongratulationStatusByCardId(Status status, long cardId) {
        congratulationDao.changeCongratulationsStatusByCardId(status, cardId);
    }

    @Override
    public void deleteById(long congratulationId) {
        congratulationDao.deleteById(congratulationId);
    }

    @Override
    public void deleteByCardId(long cardId, long userId) {
        congratulationDao.deleteByCardId(cardId, userId);
    }

    @Override
    public void deleteLinksById(List<Link> linkIdToDelete, long congratulationId) {
        congratulationDao.deleteLinksById(linkIdToDelete, congratulationId);
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

    void addLinksToLocalImagesAndAudioFiles(MultipartFile[] files_image, MultipartFile[] files_audio,
                                            List<Link> linkList) {
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
                            throw new IllegalArgumentException("Sorry, this format is not supported by the application: "
                                    .concat(linkContentType));
                        }

                        Link link = Link.builder()
                                .link("/".concat(pathToStorage).concat("/").concat(uniqueFileName))
                                .type(linkType)
                                .build();
                        log.info("saveFilesAndCreateLinks.I`m going to add link to list: {}", link.getLink());
                        linkList.add(link);

                        log.info("Path to storage: {}, uniqueFileName: {}", pathToStorage, uniqueFileName);
                        defaultAmazonService.uploadFile(multipartFile, pathToStorage + "/" + uniqueFileName);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateCongratulationById(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String,
            String> parametersMap, long congratulationId, long userId) {

        congratulationDao.updateCongratulationMessage(parametersMap.get("message"), congratulationId, userId);
        List<Link> linkList = getLinkList(files_image, files_audio, parametersMap);
        congratulationDao.saveLinks(linkList, congratulationId);
    }
}
