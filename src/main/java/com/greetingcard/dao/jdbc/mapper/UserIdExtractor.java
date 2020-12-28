package com.greetingcard.dao.jdbc.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIdExtractor implements ResultSetExtractor<Long> {
    @Override
    public Long extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        if (!resultSet.next()) {
            throw new IllegalArgumentException("No user found for requested hash");
        }

        return resultSet.getLong("user_id");
    }
}
