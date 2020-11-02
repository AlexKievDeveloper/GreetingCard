package com.greetingcard.dao.jdbc;

import com.greetingcard.ServiceLocator;
import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CongratulationRowMapper;
import com.greetingcard.dao.jdbc.mapper.CongratulationsRowMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;
import com.greetingcard.util.PropertyReader;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class JdbcCongratulationDao implements CongratulationDao {
    private static final String GET_CONGRATULATION = "SELECT congratulations.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id FROM congratulations LEFT JOIN links on congratulations.congratulation_id = links.congratulation_id WHERE congratulations.congratulation_id=?";
    private static final String SAVE_CONGRATULATION = "INSERT INTO congratulations (message, card_id, user_id, status_id) VALUES (?,?,?,?)";
    private static final String SAVE_LINK = "INSERT INTO links (link, type_id, congratulation_id) VALUES(?,?,?)";
    private static final String LEAVE_BY_CARD_ID = "DELETE FROM congratulations WHERE card_id=? and user_id=?";
    private static final String FIND_IMAGE_AND_AUDIO_LINKS_BY_CARD_ID = "SELECT link, type_id FROM links l LEFT JOIN congratulations cg ON cg.congratulation_id = l.congratulation_id where card_id=? and (type_id = 2 OR type_id = 3) and user_id =?";
    private static final String FIND_CONGRATULATIONS_BY_CARD_ID = "SELECT cg.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id FROM congratulations cg LEFT JOIN links on cg.congratulation_id = links.congratulation_id WHERE card_id=?";
    private static final String CHANGE_STATUS_CONGRATULATION_BY_CARD_ID = "UPDATE congratulations SET status_id = ? where card_id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM congratulations WHERE congratulation_id=? and user_id=?";
    private static final String FIND_IMAGE_AND_AUDIO_LINKS_BY_CONGRATULATION_ID = "SELECT link, type_id FROM links l LEFT JOIN congratulations cg ON cg.congratulation_id = l.congratulation_id where cg.congratulation_id=? and (type_id = 2 OR type_id = 3) and user_id =?";

    private static final CongratulationRowMapper CONGRATULATION_ROW_MAPPER = new CongratulationRowMapper();
    private static final CongratulationsRowMapper CONGRATULATIONS_ROW_MAPPER = new CongratulationsRowMapper();
    private final DataSource dataSource;

    public JdbcCongratulationDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CONGRATULATION)) {
            preparedStatement.setInt(1, congratulationId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return CONGRATULATION_ROW_MAPPER.mapRow(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("Exception while getting congratulation by id: {}", congratulationId, e);
            throw new RuntimeException("Exception while getting congratulation by id: " + congratulationId, e);
        }
    }

    @Override
    public void save(Congratulation congratulation) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementInCongratulations = connection.prepareStatement(SAVE_CONGRATULATION,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            statementInCongratulations.setString(1, congratulation.getMessage());
            statementInCongratulations.setLong(2, congratulation.getCard().getId());
            statementInCongratulations.setLong(3, congratulation.getUser().getId());
            statementInCongratulations.setInt(4, congratulation.getStatus().getStatusNumber());
            statementInCongratulations.execute();

            try (ResultSet resultSet = statementInCongratulations.getGeneratedKeys()) {
                resultSet.next();
                List<Link> linkList = congratulation.getLinkList();
                for (Link link : linkList) {
                    link.setCongratulationId(resultSet.getInt(1));
                }
                saveLinks(linkList, connection);
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while saving congratulation", e);
            throw new RuntimeException("Exception while saving congratulation", e);
        }
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
    public List<Congratulation> findCongratulationsByCardId(long cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CONGRATULATIONS_BY_CARD_ID)) {
            statement.setLong(1, cardId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return CONGRATULATIONS_ROW_MAPPER.mapRow(resultSet);
            }
        } catch (SQLException e) {
            log.error("Exception while select congratulations by card_id - {}", cardId, e);
            throw new RuntimeException("Exception while select congratulations by card_id ", e);
        }
    }

    @Override
    public void changeStatusCongratulationsByCardId(Status status, long cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CHANGE_STATUS_CONGRATULATION_BY_CARD_ID)) {
            statement.setInt(1, status.getStatusNumber());
            statement.setLong(2, cardId);
            statement.execute();
        } catch (SQLException e) {
            log.error("Exception while change status congratulation - {}", cardId, e);
            throw new RuntimeException("Exception while change status congratulation ", e);
        }
    }

    void saveLinks(List<Link> linkList, Connection connection) throws SQLException {
        try (PreparedStatement statementInLinks = connection.prepareStatement(SAVE_LINK)) {
            for (Link link : linkList) {
                statementInLinks.setString(1, link.getLink());
                statementInLinks.setInt(2, link.getType().getTypeNumber());
                statementInLinks.setInt(3, link.getCongratulationId());
                statementInLinks.addBatch();
            }
            statementInLinks.executeBatch();
        }
    }

    void delete(long id, long userId, String deleteQuery, String findQuery) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            statement.setLong(2, userId);
            try (PreparedStatement statementGetLinks = connection.prepareStatement(findQuery)) {
                statementGetLinks.setLong(1, id);
                statementGetLinks.setLong(2, userId);
                try (ResultSet resultSet = statementGetLinks.executeQuery()) {
                    while (resultSet.next()) {
                        String file = resultSet.getString("link");
                        try {
                            Files.deleteIfExists(Paths.get(file));
                        } catch (IOException e) {
                            log.error("Exception while deleting file - {}", file, e);
                            throw new RuntimeException("Exception while deleting file", e);
                        }
                    }
                }
            }
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            log.error("Exception while deleting congratulations by - {}", id, e);
            throw new RuntimeException("Exception while deleting congratulations ", e);
        }
    }
}
