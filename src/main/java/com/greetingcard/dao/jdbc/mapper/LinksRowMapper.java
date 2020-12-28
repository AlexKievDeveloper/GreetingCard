package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.Link;
import com.greetingcard.entity.LinkType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LinksRowMapper implements RowMapper<Link> {
    @Override
    public Link mapRow(ResultSet resultSet, int row) throws SQLException, DataAccessException {
        return Link.builder()
                .id(resultSet.getInt("link_id"))
                .link(resultSet.getString("link"))
                .congratulationId(resultSet.getInt("congratulation_id"))
                .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                .build();
    }
}

