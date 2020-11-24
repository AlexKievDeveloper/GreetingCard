package com.greetingcard.service.impl;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.file.LocalDiskFileDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CongratulationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Setter
public class DefaultCongratulationService implements CongratulationService {
    private CongratulationDao congratulationDao;
    private LocalDiskFileDao localDiskFileDao;
    private String pathToImageStorage;
    private String pathToAudioStorage;

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return congratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public List<Link> getLinkList(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String, String> parametersMap) {
        List<Link> linkList = new ArrayList<>();
        addImageLinks(linkList, parametersMap.get("image_links"));
        log.info("Service level.Created images links");
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

    void addImageLinks(List<Link> linkList, String image_links) {
        if (image_links.length() > 500) {
            throw new IllegalArgumentException("Sorry, congratulation not saved. The link is very long. " +
                    "Please use a link up to 500 characters.");
        }

        if (!image_links.equals("")) {
            List<String> plainLinksCollection = getLinksListFromText(image_links);

            for (String imageLink : plainLinksCollection) {
                Link link = Link.builder()
                        .link(imageLink)
                        .type(LinkType.PICTURE)
                        .build();
                linkList.add(link);
            }
        }
    }

    void addYoutubeLinks(List<Link> linkList, String youtubeLinks) {

        List<String> youtubeLinksCollection = getLinksListFromText(youtubeLinks);

        for (String youtubeLink : youtubeLinksCollection) {
            if (!youtubeLink.contains("youtu") || youtubeLink.length() > 500) {
                throw new IllegalArgumentException("Wrong youtube link url!");
            }
            Link video = Link.builder()
                    .link(getYoutubeVideoId(youtubeLink))
                    .type(LinkType.VIDEO)
                    .build();
            linkList.add(video);
        }
    }

    String getYoutubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?" +
                "feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

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

    List<String> getLinksListFromText(String text) {
        String pattern = "(https?:\\/\\/)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(text);

        List<String> linkList = new ArrayList<>();

        while (matcher.find()) {
            linkList.add(matcher.group());
        }

        return linkList;
    }

    void saveFilesAndCreateLinks(MultipartFile[] files, List<Link> linkList) {
        for (MultipartFile multipartFile : files) {
            if (multipartFile.getSize() > 1000 && !multipartFile.isEmpty()) {
                String salt = UUID.randomUUID().toString();
                String fileName = multipartFile.getOriginalFilename();
                String linkContentType = multipartFile.getContentType();

                if (linkContentType != null && fileName != null) {
                    String uniqueFileName = salt.concat(fileName);
                    LinkType linkType = null;
                    String pathToStorage = null;

                    switch (linkContentType) {
                        case "image/jpeg":
                        case "image/jpg":
                        case "image/png":
                            pathToStorage = pathToImageStorage;
                            linkType = LinkType.PICTURE;
                            break;
                        case "audio/mpeg":
                            pathToStorage = pathToAudioStorage;
                            linkType = LinkType.AUDIO;
                            break;
                    }

                    Link link = Link.builder()
                            .link(pathToStorage.concat(uniqueFileName))
                            .type(linkType)
                            .build();
                    linkList.add(link);

                    File file = new File(pathToStorage, uniqueFileName);
                    if (Files.notExists(Path.of(file.getPath()))) {
                        localDiskFileDao.saveFileInStorage(multipartFile, file);
                    }
                }
            }
        }
    }
}
