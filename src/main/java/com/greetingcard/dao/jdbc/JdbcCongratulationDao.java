package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CongratulationExtractor;
import com.greetingcard.dao.jdbc.mapper.CongratulationsExtractor;
import com.greetingcard.dao.jdbc.mapper.LinksRowMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import com.greetingcard.service.impl.DefaultAmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Repository
@PropertySource("classpath:queries.properties")
public class JdbcCongratulationDao implements CongratulationDao {
    private static final CongratulationExtractor CONGRATULATION_EXTRACTOR = new CongratulationExtractor();
    private static final CongratulationsExtractor CONGRATULATIONS_EXTRACTOR = new CongratulationsExtractor();
    private static final LinksRowMapper LINKS_ROW_MAPPER = new LinksRowMapper();

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private DefaultAmazonService defaultAmazonService;

    @Value("${get.congratulation}")
    private String getCongratulation;
    @Value("${get.links}")
    private String getLinks;
    @Value("${save.congratulation}")
    private String saveCongratulation;
    @Value("${update.congratulation}")
    private String updateCongratulation;
    @Value("${save.link}")
    private String saveLink;
    @Value("${leave.by.card.id}")
    private String leaveByCardId;
    @Value("${find.image.and.audio.links.by.card.id}")
    private String findImageAndAudioLinksByCardId;
    @Value("${find.congratulations.by.card.id}")
    private String findCongratulationsByCardId;
    @Value("${change.status.congratulation.by.card.id}")
    private String changeCongratulationStatusByCardId;
    @Value("${change.congratulation.status.by.congratulation.id}")
    private String changeCongratulationStatusByCongratulationId;
    @Value("${delete.by.id}")
    private String deleteById;
    @Value("${find.image.and.audio.links.by.congratulation.id}")
    private String findImageAndAudioLinksByCongratulationId;
    @Value("${delete.link.by.id}")
    private String deleteLinkById;

    public JdbcCongratulationDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, DefaultAmazonService defaultAmazonService) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.defaultAmazonService = defaultAmazonService;
    }

    @Override
    public Optional<Congratulation> getCongratulationById(long congratulationId) {
        return Optional.ofNullable(jdbcTemplate.query(getCongratulation, CONGRATULATION_EXTRACTOR, congratulationId));
    }

    @Override
    @Transactional
    public void save(@NonNull Congratulation congratulation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(saveCongratulation, new String[]{"congratulation_id"});
            statement.setString(1, congratulation.getMessage());
            statement.setLong(2, congratulation.getCardId());
            statement.setLong(3, congratulation.getUser().getId());
            statement.setInt(4, congratulation.getStatus().getStatusNumber());
            return statement;
        }, keyHolder);

        int key = Objects.requireNonNull(keyHolder.getKey()).intValue();
        saveLinks(congratulation.getLinkList(), key);
        log.debug("Added new congratulation {} to DB", congratulation.getId());
    }

    @Override
    @Transactional
    public void deleteByCardId(long cardId, long userId) {
        deleteCongratulationFiles(cardId, userId, findImageAndAudioLinksByCardId);
        Map<String, Long> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("card_id", cardId);
        namedJdbcTemplate.update(leaveByCardId, params);
    }

    @Override
    @Transactional
    public void deleteById(long congratulationId, long userId) {
        deleteCongratulationFiles(congratulationId, userId, findImageAndAudioLinksByCongratulationId);
        Map<String, Long> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("congratulation_id", congratulationId);
        namedJdbcTemplate.update(deleteById, params);
    }

    @Override
    @Transactional
    public void deleteLinksById(List<Link> linkIdToDelete, long congratulationId) {
        deleteFilesFromLinks(linkIdToDelete, congratulationId);
        if (linkIdToDelete.size() > 0) {
            MapSqlParameterSource params = getMapSqlParameterSourceForList(linkIdToDelete);
            String sql = deleteLinkById + getNamesOfParams(params.getParameterNames()) + " and congratulation_id = congratulation_id";
            params.addValue("congratulation_id", congratulationId);
            namedJdbcTemplate.update(sql, params);
        }
    }

    @Override
    public List<Congratulation> findCongratulationsByCardId(long cardId) {
        return jdbcTemplate.query(findCongratulationsByCardId, CONGRATULATIONS_EXTRACTOR, cardId);
    }

    @Override
    public void changeCongratulationsStatusByCardId(Status status, long cardId) {
        jdbcTemplate.update(changeCongratulationStatusByCardId, status.getStatusNumber(), cardId);
    }

    @Override
    public void changeCongratulationStatusByCongratulationId(Status status, long congratulationId) {
        jdbcTemplate.update(changeCongratulationStatusByCongratulationId, status.getStatusNumber(), congratulationId);
    }

    @Override
    public void updateCongratulationMessage(String message, long congratulationId, long userId) {
        jdbcTemplate.update(updateCongratulation, message, congratulationId, userId);
    }

    @Override
    public void saveLinks(List<Link> linkList, long congratulationId) {
        jdbcTemplate.batchUpdate(
                saveLink,
                linkList,
                linkList.size(),
                (statementInLinks, link) -> {
                    statementInLinks.setString(1, link.getLink());
                    statementInLinks.setInt(2, link.getType().getTypeNumber());
                    statementInLinks.setLong(3, congratulationId);
                });
    }

    void deleteCongratulationFiles(long id, long userId, String findQuery) {
        List<String> linkList = jdbcTemplate.queryForList(findQuery, String.class, id, userId);
        for (String link : linkList) {
            defaultAmazonService.deleteFileFromS3Bucket(link);
        }
    }

    void deleteFilesFromLinks(List<Link> linkIdToDelete, long congratulationId) {
        List<Link> linkList = getLinksList(linkIdToDelete, congratulationId);
        for (Link link : linkList) {
            defaultAmazonService.deleteFileFromS3Bucket(link.getLink());
        }
    }

    List<Link> getLinksList(List<Link> linkList, long congratulationId) {
        if (linkList.size() > 0) {
            MapSqlParameterSource params = getMapSqlParameterSourceForList(linkList);
            String sql = getLinks + getNamesOfParams(params.getParameterNames()) + "and congratulation_id = congratulation_id and (type_id = 2 OR type_id = 3)";
            params.addValue("congratulation_id", congratulationId);
            return namedJdbcTemplate.query(sql, params, LINKS_ROW_MAPPER);
        }
        return List.of();
    }

    String getNamesOfParams(String[] listParams) {
        StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
        for (String listParam : listParams) {
            stringJoiner.add(":" + listParam);
        }
        return stringJoiner.toString();
    }

    MapSqlParameterSource getMapSqlParameterSourceForList(List<Link> listLinkIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (int i = 0; i < listLinkIds.size(); i++) {
            long userId = listLinkIds.get(i).getId();
            String paramName = "link_id" + i;
            params.addValue(paramName, userId);
        }
        return params;
    }
}
