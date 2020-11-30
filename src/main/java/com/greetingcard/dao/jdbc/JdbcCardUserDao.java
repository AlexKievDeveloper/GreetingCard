package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.dao.jdbc.mapper.UserInfoRowMapper;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Setter
@Slf4j
public class JdbcCardUserDao implements CardUserDao {
    private static final String INSERT_MEMBER_USER = "INSERT INTO users_cards (user_id, card_id, role_id) VALUES (:user_id, :card_id, "
            + Role.MEMBER.getRoleNumber() + ")";
    private static final String GET_USER_ROLE = "SELECT role_id FROM users_cards WHERE user_id = :user_id AND card_id = :card_id";
    private static final String GET_USERS_BY_CARD_ID = "SELECT u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto, count(cg.card_id) AS countCongratulations " +
            "FROM users_cards uc JOIN users u ON (u.user_id = uc.user_id) " +
            "LEFT JOIN congratulations cg ON (uc.card_id = cg.card_id AND uc.user_id = cg.user_id) " +
            "WHERE uc.card_id = :card_id " +
            "AND   uc.role_id != " + Role.ADMIN.getRoleNumber() +
            " GROUP BY u.user_id, u.firstName, u.lastName, u.login, u.email, u.pathToPhoto " +
            " ORDER BY u.login";
    private static final String DELETE_USER = "DELETE from users_cards WHERE user_id = :user_id AND card_id = :card_id";
    private static final String DELETE_LIST_USERS = "DELETE from users_cards WHERE card_id = :card_id AND user_id IN ";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void addUserMember(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        namedParameterJdbcTemplate.update(INSERT_MEMBER_USER, params);
    }

    @Override
    public Optional<Role> getUserRole(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        List<Integer> roles = namedParameterJdbcTemplate.queryForList(GET_USER_ROLE, params, Integer.class);
        return (roles.size() > 0 ? Optional.of(Role.getByNumber(roles.get(0))) : Optional.empty());
    }

    @Override
    public List<UserInfo> getUserMembersByCardId(long cardId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("card_id", cardId);
        log.debug(GET_USERS_BY_CARD_ID);
        return namedParameterJdbcTemplate.query(GET_USERS_BY_CARD_ID, namedParameters, new UserInfoRowMapper());
    }

    @Override
    public void deleteUserFromCard(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        namedParameterJdbcTemplate.update(DELETE_USER, params);
    }

    @Override
    public void deleteListUsers(long cardId, List<UserInfo> listUserIds) {
        if (listUserIds.size() > 0) {
            MapSqlParameterSource params = getMapSqlParameterSourceForList(listUserIds);
            String sql = DELETE_LIST_USERS + getNamesOfParams(params.getParameterNames());
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
