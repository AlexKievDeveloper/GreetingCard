package com.greetingcard.dao.jdbc.mapper;
import com.greetingcard.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
public class UserSaltRowMapper implements ResultSetExtractor<User> {
    @Override
    public User extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        if (!resultSet.next()) {
            return null;
        }
        return User.builder()
                .salt(resultSet.getString("salt"))
                .build();
    }
}