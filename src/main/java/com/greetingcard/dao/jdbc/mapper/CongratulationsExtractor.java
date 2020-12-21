package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CongratulationsExtractor implements ResultSetExtractor<List<Congratulation>> {
    @Override
    public List<Congratulation> extractData(ResultSet resultSet) throws SQLException {
        Map<Long, Congratulation> congratulationMap = new HashMap<>();
        while (resultSet.next()) {
            long congratulationId = resultSet.getLong("congratulation_id");
            if (congratulationId != 0 && !congratulationMap.containsKey(congratulationId)) {
                User user = User.builder().id(resultSet.getLong("user_id")).build();
                Congratulation congratulation = Congratulation.builder()
                        .id(congratulationId)
                        .user(user)
                        .cardId(resultSet.getLong("card_id"))
                        .message(resultSet.getString("message"))
                        .status(Status.getByNumber(resultSet.getInt("status_id")))
                        .linkList(new ArrayList<>())
                        .build();
                congratulationMap.put(congratulationId, congratulation);
            }

            int linkId = resultSet.getInt("link_id");
            if (linkId != 0) {
                Link link = Link.builder()
                        .id(linkId)
                        .link(resultSet.getString("link"))
                        .type(LinkType.getByNumber(resultSet.getInt("type_id")))
                        .build();
                congratulationMap.get(congratulationId).getLinkList().add(link);
            }
        }
        return new ArrayList<>(congratulationMap.values());
    }
}
