package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardAndCongratulationRowMapper implements ResultSetExtractor<Card> {
    @Override
    public Card extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Card card = null;
        List<Congratulation> congratulationList = new ArrayList<>();
        Map<Long, Congratulation> congratulationMap = new HashMap<>();

        while (resultSet.next()) {
            if (card == null) {
                User user = User.builder()
                        .id(resultSet.getLong("card_user"))
                        .build();
                card = Card.builder()
                        .id((resultSet.getLong("card_id")))
                        .user(user)
                        .name(resultSet.getString("name"))
                        .backgroundImage(resultSet.getString("background_image"))
                        .cardLink(resultSet.getString("card_link"))
                        .status(Status.getByNumber(resultSet.getInt("status_id")))
                        .congratulationList(congratulationList)
                        .build();
            }
            long congratulation_id = resultSet.getLong("congratulation_id");
            if (congratulation_id != 0 && !congratulationMap.containsKey(congratulation_id)) {
                User user = User.builder()
                        .id(resultSet.getLong("user_id"))
                        .firstName(resultSet.getString("firstName"))
                        .lastName(resultSet.getString("lastName"))
                        .pathToPhoto(resultSet.getString("pathToPhoto"))
                        .login(resultSet.getString("login"))
                        .build();
                Congratulation congratulation = Congratulation.builder()
                        .id(congratulation_id)
                        .user(user)
                        .cardId(card.getId())
                        .message(resultSet.getString("message"))
                        .status(Status.getByNumber(resultSet.getInt("con_status")))
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
            if (card != null) {
                card.setCongratulationList(new ArrayList<>(congratulationMap.values()));
            }
        }
        return card;
    }
}
