package com.greetingcard.service.impl;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.file.LocalDiskFileDao;
import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.util.PropertyReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCongratulationService implements CongratulationService {
    private final JdbcCongratulationDao jdbcCongratulationDao;
    private final LocalDiskFileDao localDiskFileDao;
    private final PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
/*    private final String pathToImageStorage = propertyReader.getProperty("img.storage.path");
    private final String pathToAudioStorage = propertyReader.getProperty("audio.storage.path");*/

    public DefaultCongratulationService(JdbcCongratulationDao jdbcCongratulationDao, LocalDiskFileDao localDiskFileDao) {
        this.jdbcCongratulationDao = jdbcCongratulationDao;
        this.localDiskFileDao = localDiskFileDao;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return jdbcCongratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public List<Link> getLinkList(String youtubeLinks, String plainLinks) {
        List<Link> linkList = new ArrayList<>();
        addYoutubeLinks(linkList, youtubeLinks);
        //addLinksToImagesAndAudioFiles(partList, linkList);
        addPlainLinks(linkList, plainLinks);
        return linkList;
    }

    @Override
    public void save(Congratulation congratulation) {
        jdbcCongratulationDao.save(congratulation);
    }

    void addYoutubeLinks(List<Link> linkList, String youtubeLinks) {

        String pattern = "(https?:\\/\\/)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeLinks);

        List<String> youtubeLinksCollection = new ArrayList<>();

        while (matcher.find()) {
            youtubeLinksCollection.add(matcher.group());
        }

        for (String youtubeLink : youtubeLinksCollection) {

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

/*    void addLinksToImagesAndAudioFiles(Collection<Part> partList, List<Link> linkList) {

        for (Part part : partList) {
            if (part.getSize() > 1000) {
                String salt = UUID.randomUUID().toString();
                String fileName = part.getSubmittedFileName();

                String uniqueFileName = salt.concat(fileName);

                if ("image/jpeg".equals(part.getContentType()) || "image/png".equals(part.getContentType())) {
                    Link picture = Link.builder()
                            .link(pathToImageStorage.concat(salt).concat(uniqueFileName))
                            .type(LinkType.PICTURE)
                            .build();
                    linkList.add(picture);

                    File pictureFile = new File(pathToImageStorage, uniqueFileName);
                    if (Files.notExists(Path.of(pictureFile.getPath()))) {
                        localDiskFileDao.saveFileInStorage(part, pictureFile);
                    }
                } else if ("audio/mpeg".equals(part.getContentType())) {
                    Link audio = Link.builder()
                            .link(pathToAudioStorage.concat(uniqueFileName))
                            .type(LinkType.AUDIO)
                            .build();
                    linkList.add(audio);

                    File audioFile = new File(pathToAudioStorage, uniqueFileName);
                    if (Files.notExists(Path.of(audioFile.getPath()))) {
                        localDiskFileDao.saveFileInStorage(part, audioFile);
                    }
                }
            }
        }
    }*/

    void addPlainLinks(List<Link> linkList, String plainLinks) {
        if (!plainLinks.equals("")) {

            String pattern = "(https?:\\/\\/)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*";

            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(plainLinks);

            List<String> plainLinksCollection = new ArrayList<>();

            while (matcher.find()) {
                plainLinksCollection.add(matcher.group());
            }

            for (String plainLink : plainLinksCollection) {
                Link link = Link.builder()
                        .link(plainLink)
                        .type(LinkType.PLAIN_LINK)
                        .build();
                linkList.add(link);
            }
        }
    }
}