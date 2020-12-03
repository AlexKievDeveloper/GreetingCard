package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinksRowMapper implements ResultSetExtractor<List<Link>> {

    @Override
    public List<Link> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<Link> linkList = new ArrayList<>();

        if (!resultSet.next()) {
            return linkList;
        }

        do {
            int linkId = resultSet.getInt("link_id");
            if (linkId != 0) {
                Link link = Link.builder()
                        .id(linkId)
                        .link(resultSet.getString("link"))
                        .congratulationId(resultSet.getInt("congratulation_id"))
                        .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                        .build();

                linkList.add(link);
            }
        } while (resultSet.next());
        return linkList;
    }
}
