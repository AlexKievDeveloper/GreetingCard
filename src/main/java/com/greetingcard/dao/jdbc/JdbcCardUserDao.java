package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.dao.jdbc.mapper.UserInfoRowMapper;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Slf4j
@Repository
@PropertySource("classpath:queries.properties")
public class JdbcCardUserDao implements CardUserDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${insert.member.user}")
    private String insertMemberUser;
    @Value("${get.user.role}")
    private String getUserRole;
    @Value("${get.users.by.card.id}")
    private String getUsersByCardId;
    @Value("${delete.user}")
    private String deleteUser;
    @Value("${delete.list.users}")
    private String deleteListUsers;

    @Override
    public void addUserMember(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        namedParameterJdbcTemplate.update(insertMemberUser, params);
    }

    @Override
    public Optional<Role> getUserRole(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        List<Integer> roles = namedParameterJdbcTemplate.queryForList(getUserRole, params, Integer.class);
        return (roles.size() > 0 ? Optional.of(Role.getByNumber(roles.get(0))) : Optional.empty());
    }

    @Override
    public List<UserInfo> getUserMembersByCardId(long cardId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("card_id", cardId);
        log.debug(getUsersByCardId);
        return namedParameterJdbcTemplate.query(getUsersByCardId, namedParameters, new UserInfoRowMapper());
    }

    @Override
    public void deleteUserFromCard(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        namedParameterJdbcTemplate.update(deleteUser, params);
    }

    @Override
    public void deleteListUsers(long cardId, List<UserInfo> listUserIds) {
        if (listUserIds.size() > 0) {
            MapSqlParameterSource params = getMapSqlParameterSourceForList(listUserIds);
            String sql = deleteListUsers + getNamesOfParams(params.getParameterNames());
            params.addValue("card_id", cardId);
            namedParameterJdbcTemplate.update(sql, params);
        }
    }

    String getNamesOfParams(String[] listParams) {
        StringJoiner stringJoiner = new StringJoiner(",", "(", ")");
        for (String listParam : listParams) {
            stringJoiner.add(":" + listParam);
        }
        return stringJoiner.toString();
    }

    private MapSqlParameterSource getMapSqlParameterSourceForList(List<UserInfo> listUserIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (int i = 0; i < listUserIds.size(); i++) {
            long userId = listUserIds.get(i).getId();
            String paramName = "userId" + i;
            params.addValue(paramName, userId);
        }
        return params;
    }

    private MapSqlParameterSource getSqlParameterSource(long cardId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId)
                .addValue("card_id", cardId);
        return params;
    }
}
