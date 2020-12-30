package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.dao.jdbc.mapper.UserInfoRowMapper;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.UserInfo;
import com.greetingcard.entity.UserOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcCardUserDao implements CardUserDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private String insertMemberUser;
    @Autowired
    private String addToCardsHashes;
    @Autowired
    private String getUserRole;
    @Autowired
    private String getCardsHashes;
    @Autowired
    private String getUsersByCardId;
    @Autowired
    private String getUsersByCardIdForWebSocketNotification;
    @Autowired
    private String updateUsersOrder;
    @Autowired
    private String deleteUser;
    @Autowired
    private String deleteListUsers;

    @Override
    public void addUserMember(long cardId, long userId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, userId);
        namedParameterJdbcTemplate.update(insertMemberUser, params);
    }

    @Override
    public void saveHash(long cardId, String hash) {
        MapSqlParameterSource params = getSqlParameterSource(cardId, hash);
        namedParameterJdbcTemplate.update(addToCardsHashes, params);
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
    public List<UserInfo> getUserMembersByCardIdForWebSocketNotification(long cardId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("card_id", cardId);
        log.debug(getUsersByCardIdForWebSocketNotification);
        return namedParameterJdbcTemplate.query(getUsersByCardIdForWebSocketNotification, namedParameters, new UserInfoRowMapper());
    }

    @Override
    public List<String> getCardHashesByCardId(long cardId) {
        MapSqlParameterSource params = getSqlParameterSource(cardId);
        return namedParameterJdbcTemplate.queryForList(getCardsHashes, params, String.class);
    }

    @Override
    @Transactional
    public void changeUsersOrder(long cardId, List<UserOrder> usersOrder) {
        for (UserOrder userOrder : usersOrder) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("users_order", userOrder.getOrder());
            params.addValue("user_id", userOrder.getId());
            params.addValue("card_id", cardId);
            namedParameterJdbcTemplate.update(updateUsersOrder, params);
        }
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

    private MapSqlParameterSource getSqlParameterSource(long cardId, String hash) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("card_id", cardId)
                 .addValue("hash", hash);
        return params;
    }

    private MapSqlParameterSource getSqlParameterSource(long cardId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("card_id", cardId);
        return params;
    }
}
