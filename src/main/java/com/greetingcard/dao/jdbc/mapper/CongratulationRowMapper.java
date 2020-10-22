package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import com.greetingcard.entity.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CongratulationRowMapper {
    public Congratulation mapRow(ResultSet resultSet) throws SQLException {

        Congratulation congratulation = Congratulation.builder()
                .id(resultSet.getInt("congratulation_id"))
                .message(resultSet.getString("message"))
                .cardId(resultSet.getInt("card_id"))
                .userId(resultSet.getInt("user_id"))
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .build();

        Link firstLink = Link.builder()
                .id(resultSet.getInt("link_id"))
                .link(resultSet.getString("link"))
                .congratulationId(resultSet.getInt("congratulation_id"))
                .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                .build();

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
