package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CongratulationService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCongratulationService implements CongratulationService {
    private final JdbcCongratulationDao jdbcCongratulationDao;

    public DefaultCongratulationService(JdbcCongratulationDao jdbcCongratulationDao) {
        this.jdbcCongratulationDao = jdbcCongratulationDao;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return jdbcCongratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public List<Link> getLinkList(String youtubeLinks, String plainLinks) {
        List<Link> linkList = new ArrayList<>();
        addYoutubeLinks(linkList, youtubeLinks);
        addPlainLinks(linkList, plainLinks);
        return linkList;
    }

    @Override
    public void save(Congratulation congratulation) {
        jdbcCongratulationDao.save(congratulation);
    }

    @Override
    public void changeCongratulationStatusByCongratulationId(Status status, long congratulationId) {
        jdbcCongratulationDao.changeCongratulationStatusByCongratulationId(status, congratulationId);
    }

    @Override
    public void deleteById(long congratulationId, long userId) {
        jdbcCongratulationDao.deleteById(congratulationId, userId);
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