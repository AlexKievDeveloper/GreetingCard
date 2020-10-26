package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CongratulationDao;
import com.greetingcard.dao.jdbc.mapper.CongratulationRowMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class JdbcCongratulationDao implements CongratulationDao {
    private static final String GET_CONGRATULATION = "SELECT congratulations.congratulation_id, user_id, card_id, status_id, message, link_id, link, type_id FROM congratulations LEFT JOIN links on congratulations.congratulation_id = links.congratulation_id WHERE congratulations.congratulation_id=?;";
    private static final String SAVE_CONGRATULATION = "INSERT INTO congratulations (message, card_id, user_id, status_id) VALUES (?,?,?,?)";
    private static final String SAVE_LINK = "INSERT INTO links (link, type_id, congratulation_id) VALUES(?,?,?)";
    private static final CongratulationRowMapper CONGRATULATION_ROW_MAPPER = new CongratulationRowMapper();
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
                if (resultSet.next()){
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
            statementInCongratulations.setInt(2, congratulation.getCard().getId());
            statementInCongratulations.setInt(3, congratulation.getUser().getId());
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
}
