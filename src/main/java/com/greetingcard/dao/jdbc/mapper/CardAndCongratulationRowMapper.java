package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardAndCongratulationRowMapper {
    public Card mapRow(ResultSet resultSet) throws SQLException {
        Card card = null;
        List<Congratulation> congratulationList = new ArrayList<>();
        Map<Integer, Congratulation> congratulationMap = new HashMap<>();
        while (resultSet.next()) {
            if (card == null) {
                card = Card.builder()
                        .id((resultSet.getInt("card_id")))
                        .name(resultSet.getString("name"))
                        .backgroundImage(resultSet.getString("background_image"))
                        .cardLink(resultSet.getString("card_link"))
                        .status(Status.getByNumber(resultSet.getInt("status_id")))
                        .congratulationList(congratulationList)
                        .build();
            }
            int congratulation_id = resultSet.getInt("congratulation_id");
            if (congratulation_id != 0 && !congratulationMap.containsKey(congratulation_id)) {
                User user = User.builder()
                        .id(resultSet.getInt("user_id"))
                        .firstName(resultSet.getString("firstName"))
                        .lastName(resultSet.getString("lastName"))
                        .login(resultSet.getString("login"))
                        .build();
                Congratulation congratulation = Congratulation.builder()
                        .id(congratulation_id)
                        .user(user)
                        .card(card)
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
            if (card!=null){
                card.setCongratulationList(new ArrayList<>(congratulationMap.values()));
            }
        }
        return card;
    }
}
