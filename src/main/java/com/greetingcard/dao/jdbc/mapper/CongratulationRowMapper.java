package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CongratulationRowMapper implements ResultSetExtractor<Congratulation> {

    @Override
    public Congratulation extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        if (!resultSet.next()) {
            return null;
        }

        Congratulation congratulation = Congratulation.builder()
                .id(resultSet.getInt("congratulation_id"))
                .message(resultSet.getString("message"))
                .cardId(resultSet.getLong("card_id"))
                .user(User.builder().id(resultSet.getInt("user_id")).build())
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();

        Link firstLink = null;
        if (resultSet.getInt("link_id") != 0) {
            firstLink = Link.builder()
                    .id(resultSet.getInt("link_id"))
                    .link(resultSet.getString("link"))
                    .congratulationId(resultSet.getInt("congratulation_id"))
                    .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                    .build();
        }

        List<Link> linkList = new ArrayList<>();
        linkList.add(firstLink);

        while (resultSet.next()) {
            Link link = Link.builder()
                    .id(resultSet.getInt("link_id"))
                    .link(resultSet.getString("link"))
                    .congratulationId(resultSet.getInt("congratulation_id"))
                    .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                    .build();

            linkList.add(link);
        }

        congratulation.setLinkList(linkList);
        return congratulation;
    }
}
