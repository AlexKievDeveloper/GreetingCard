package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CongratulationRowMapper;
import com.greetingcard.dao.jdbc.mapper.CongratulationsRowMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Setter
public class JdbcCongratulationDao implements CongratulationDao {
    private static final String GET_CONGRATULATION = "SELECT congratulations.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id FROM congratulations LEFT JOIN links on congratulations.congratulation_id = links.congratulation_id WHERE congratulations.congratulation_id=?";
    private static final String GET_LINKS = "SELECT link_id, link, type_id, congratulation_id FROM links where link_id = ? and congratulation_id = ? and (type_id = 2 OR type_id = 3)";
    private static final String SAVE_CONGRATULATION = "INSERT INTO congratulations (message, card_id, user_id, status_id) VALUES (?,?,?,?)";
    private static final String UPDATE_CONGRATULATION = "UPDATE congratulations SET message = ? where (congratulation_id = ? and user_id = ?)";
    private static final String SAVE_LINK = "INSERT INTO links (link, type_id, congratulation_id) VALUES(?,?,?)";
    private static final String LEAVE_BY_CARD_ID = "DELETE FROM congratulations WHERE card_id=? and user_id=?";
    private static final String FIND_IMAGE_AND_AUDIO_LINKS_BY_CARD_ID = "SELECT link FROM links l LEFT JOIN congratulations cg ON cg.congratulation_id = l.congratulation_id where card_id=? and (type_id = 2 OR type_id = 3) and user_id =?";
    private static final String FIND_CONGRATULATIONS_BY_CARD_ID = "SELECT cg.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id FROM congratulations cg LEFT JOIN links on cg.congratulation_id = links.congratulation_id WHERE card_id=?";
    private static final String CHANGE_STATUS_CONGRATULATION_BY_CARD_ID = "UPDATE congratulations SET status_id = ? where card_id = ?";
    private static final String CHANGE_CONGRATULATION_STATUS_BY_CONGRATULATION_ID = "UPDATE congratulations SET status_id = ? where congratulation_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM congratulations WHERE congratulation_id=? and user_id=?";
    private static final String FIND_IMAGE_AND_AUDIO_LINKS_BY_CONGRATULATION_ID = "SELECT link FROM links l LEFT JOIN congratulations cg ON cg.congratulation_id = l.congratulation_id where cg.congratulation_id=? and (type_id = 2 OR type_id = 3) and user_id =?";
    private static final String DELETE_LINK_BY_ID = "DELETE FROM links WHERE link_id=? and congratulation_id=?";

    private static final CongratulationRowMapper CONGRATULATION_ROW_MAPPER = new CongratulationRowMapper();
    private static final CongratulationsRowMapper CONGRATULATIONS_ROW_MAPPER = new CongratulationsRowMapper();
    private JdbcTemplate jdbcTemplate;

    public Congratulation getCongratulationById(long congratulationId) {
        return jdbcTemplate.query(GET_CONGRATULATION, CONGRATULATION_ROW_MAPPER, congratulationId);
    }

    @Override
    public void save(@NonNull Congratulation congratulation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(SAVE_CONGRATULATION, new String[]{"congratulation_id"});
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
    public void deleteByCardId(long cardId, long userId) {
        delete(cardId, userId, LEAVE_BY_CARD_ID, FIND_IMAGE_AND_AUDIO_LINKS_BY_CARD_ID);
    }

    @Override
    public void deleteById(long congratulationId, long userId) {
        delete(congratulationId, userId, DELETE_BY_ID, FIND_IMAGE_AND_AUDIO_LINKS_BY_CONGRATULATION_ID);
    }

    @Override
    public void saveLinks(List<Link> linkList, long congratulationId) {
        jdbcTemplate.update(connection -> {
            connection.setAutoCommit(false);
            PreparedStatement statementInLinks = connection.prepareStatement(SAVE_LINK);
            for (Link link : linkList) {
                log.info("Dao level. Saving link: {}", link);
                statementInLinks.setString(1, link.getLink());
                statementInLinks.setInt(2, link.getType().getTypeNumber());
                statementInLinks.setLong(3, congratulationId);
                statementInLinks.addBatch();
            }

            statementInLinks.executeBatch();
            connection.commit();

            return statementInLinks;
        });
    }

    @Override
    public void deleteLinksById(List<Link> linkIdToDelete, long congratulationId) {
        jdbcTemplate.update(connection -> {
            connection.setAutoCommit(false);
            PreparedStatement statementInLinks = connection.prepareStatement(DELETE_LINK_BY_ID);

            for (Link link : linkIdToDelete) {
                log.info("Dao level. Removing link: {}", link);
                statementInLinks.setLong(1, link.getId());
                statementInLinks.setLong(2, congratulationId);
                statementInLinks.addBatch();
            }

            statementInLinks.executeBatch();
            connection.commit();

            return statementInLinks;
        });
    }

    @Override
    public List<Congratulation> findCongratulationsByCardId(long cardId) {
        return jdbcTemplate.query(FIND_CONGRATULATIONS_BY_CARD_ID, CONGRATULATIONS_ROW_MAPPER, cardId);
    }

    @Override
    public void changeStatusCongratulationsByCardId(Status status, long cardId) {
        jdbcTemplate.update(CHANGE_STATUS_CONGRATULATION_BY_CARD_ID, status.getStatusNumber(), cardId);
    }

    @Override
    public void changeCongratulationStatusByCongratulationId(Status status, long congratulationId) {
        jdbcTemplate.update(CHANGE_CONGRATULATION_STATUS_BY_CONGRATULATION_ID, status.getStatusNumber(), congratulationId);
    }

    @Override
    public void updateCongratulation(String message, long congratulationId, long userId) {
        jdbcTemplate.update(UPDATE_CONGRATULATION, message, congratulationId, userId);
    }

    void delete(long id, long userId, String deleteQuery, String findQuery) {

        List<String> linkList = jdbcTemplate.queryForList(findQuery, String.class, id, userId);

        for (String link : linkList) {

            try {
                Files.deleteIfExists(Paths.get(link));
            } catch (IOException e) {
                log.error("Exception while deleting file - {}", link, e);
                throw new RuntimeException("Exception while deleting file", e);
            }
        }
        jdbcTemplate.update(deleteQuery, id, userId);
    }
}
