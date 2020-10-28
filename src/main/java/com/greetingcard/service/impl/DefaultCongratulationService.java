package com.greetingcard.service.impl;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.file.LocalDiskFileDao;
import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.util.PropertyReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCongratulationService implements CongratulationService {
    private final JdbcCongratulationDao jdbcCongratulationDao;
    private final LocalDiskFileDao localDiskFileDao;
    private final PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private final String pathToImageStorage = propertyReader.getProperty("img.storage.path");
    private final String pathToAudioStorage = propertyReader.getProperty("audio.storage.path");

    public DefaultCongratulationService(JdbcCongratulationDao jdbcCongratulationDao, LocalDiskFileDao localDiskFileDao) {
        this.jdbcCongratulationDao = jdbcCongratulationDao;
        this.localDiskFileDao = localDiskFileDao;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return jdbcCongratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public void save(Congratulation congratulation) {
        jdbcCongratulationDao.save(congratulation);
    }

    @Override
    public List<Link> getLinkList(List<Part> partList, HttpServletRequest request) {
        String youtubeLinks = request.getParameter("youtube");

        String plainLinks = request.getParameter("plain-link");

        List<Link> linkList = new ArrayList<>();
        addYoutubeLinks(linkList, youtubeLinks);
        addLinksToImagesAndAudioFiles(partList, linkList);
        addPlainLinks(linkList, plainLinks);
        return linkList;
    }

    void addYoutubeLinks(List<Link> linkList, String youtubeLinks) {

        String[] youtubeLinksArray = youtubeLinks.split("\\r?\\n");

        for (String youtubeLink : youtubeLinksArray) {

            if (youtubeLink.contains("youtu")) {
                Link video = Link.builder()
                        .link(getYoutubeVideoId(youtubeLink))
                        .type(LinkType.VIDEO)
                        .build();
                linkList.add(video);
            }
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

    void addLinksToImagesAndAudioFiles(List<Part> partList, List<Link> linkList) {

        for (Part part : partList) {

            String fileName = part.getSubmittedFileName();

            if ("image/jpeg".equals(part.getContentType()) || "image/png".equals(part.getContentType())) {
                Link picture = Link.builder()
                        .link(pathToImageStorage.concat(fileName))
                        .type(LinkType.PICTURE)
                        .build();
                linkList.add(picture);

                File pictureFile = new File(pathToImageStorage.concat(fileName));
                localDiskFileDao.saveFileInStorage(part, pictureFile);
            } else if ("audio/mpeg".equals(part.getContentType())) {
                Link audio = Link.builder()
                        .link(pathToAudioStorage.concat(fileName))
                        .type(LinkType.AUDIO)
                        .build();
                linkList.add(audio);

                File audioFile = new File(pathToAudioStorage.concat(fileName));
                localDiskFileDao.saveFileInStorage(part, audioFile);
            }
        }
    }

    void addPlainLinks(List<Link> linkList, String plainLinks) {
        if (!plainLinks.equals("")) {
            String[] plainLinksArray = plainLinks.split("\\r?\\n");
            for (String plainLink : plainLinksArray) {
                Link link = Link.builder()
                        .link(plainLink)
                        .type(LinkType.PLAIN_LINK)
                        .build();
                linkList.add(link);
            }
        }
    }
}
