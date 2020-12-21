package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CongratulationExtractor implements ResultSetExtractor<Congratulation> {
    @Override
    public Congratulation extractData(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            throw new IllegalArgumentException("Empty result set for requested congratulation");
        }

        Congratulation congratulation = Congratulation.builder()
                .id(resultSet.getInt("congratulation_id"))
                .message(resultSet.getString("message"))
                .cardId(resultSet.getLong("card_id"))
                .user(User.builder().id(resultSet.getInt("user_id")).build())
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();

        List<Link> linkList = new ArrayList<>();
        if (resultSet.getInt("link_id") != 0) {
            do {
                Link link = Link.builder()
                        .id(resultSet.getInt("link_id"))
                        .link(resultSet.getString("link"))
                        .congratulationId(resultSet.getInt("congratulation_id"))
                        .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                        .build();

                linkList.add(link);
            } while (resultSet.next());
        }
        congratulation.setLinkList(linkList);
        return congratulation;
    }
}
