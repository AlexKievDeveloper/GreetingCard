package com.greetingcard.dao.jdbc;

import com.greetingcard.dao.CardUserDao;
import com.greetingcard.entity.Role;
import lombok.Setter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;

@Setter
public class JdbcCardUserDao implements CardUserDao {
    private static final String INSERT_MEMBER_USER = "INSERT INTO users_cards (user_id, card_id, role_id) VALUES (:user_id, :card_id, "
            + Role.MEMBER.getRoleNumber() + ")";
    private static final String GET_USER_ROLE = "SELECT role_id FROM users_cards WHERE user_id = :user_id AND card_id = :card_id";

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

    private MapSqlParameterSource getSqlParameterSource(long cardId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId)
                .addValue("card_id", cardId);
        return params;
    }
}
