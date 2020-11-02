package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CongratulationsRowMapper {
    public List<Congratulation> mapRow(ResultSet resultSet) throws SQLException {
        Map<Long, Congratulation> congratulationMap = new HashMap<>();
        Card card = null;
        while (resultSet.next()) {
            if (card == null) {
                card = Card.builder().id(resultSet.getLong("card_id")).build();
            }
            long congratulation_id = resultSet.getLong("congratulation_id");
            if (congratulation_id != 0 && !congratulationMap.containsKey(congratulation_id)) {
                User user = User.builder().id(resultSet.getLong("user_id")).build();
                Congratulation congratulation = Congratulation.builder()
                        .id(congratulation_id)
                        .user(user)
                        .card(card)
                        .message(resultSet.getString("message"))
                        .status(Status.getByNumber(resultSet.getInt("status_id")))
                        .linkList(new ArrayList<>())
                        .build();
                congratulationMap.put(congratulation_id, congratulation);
            }

            int linkId = resultSet.getInt("link_id");
            if (linkId != 0) {
                Link link = Link.builder()
                        .id(linkId)
                        .link(resultSet.getString("link"))
                        .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                        .build();
                congratulationMap.get(congratulation_id).getLinkList().add(link);
            }
        }
        return new ArrayList<>(congratulationMap.values());
    }
}