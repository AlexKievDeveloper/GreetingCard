package com.greetingcard.dao.jdbc.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIdRowMapper implements ResultSetExtractor<Long> {

    @Override
    public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        if (!resultSet.next()) {
            return null;
        }

        return resultSet.getLong("user_id");
    }
}
