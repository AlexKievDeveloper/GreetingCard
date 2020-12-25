package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardAndCongratulationExtractor implements ResultSetExtractor<Card> {
    @Override
    public Card extractData(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            throw new IllegalArgumentException("Sorry, you do not have access rights to the card or the card does not exist");
        }

        List<Congratulation> congratulationList = new ArrayList<>();
        Map<Long, Congratulation> congratulationMap = new HashMap<>();

        User user = User.builder()
                .id(resultSet.getLong("card_user"))
                .build();
        Card card = Card.builder()
                .id((resultSet.getLong("card_id")))
                .user(user)
                .name(resultSet.getString("name"))
                .backgroundImage(resultSet.getString("background_image"))
                .backgroundCongratulations(resultSet.getString("background_congratulations"))
                .cardLink(resultSet.getString("card_link"))
                .status(Status.getByNumber(resultSet.getInt("status_id")))
                .congratulationList(congratulationList)
                .build();

        do {
            long congratulationId = resultSet.getLong("congratulation_id");
            if (congratulationId != 0 && !congratulationMap.containsKey(congratulationId)) {
                User userAuthorOfCongratulation = User.builder()
                        .id(resultSet.getLong("user_id"))
                        .firstName(resultSet.getString("firstName"))
                        .lastName(resultSet.getString("lastName"))
                        .login(resultSet.getString("login"))
                        .pathToPhoto(resultSet.getString("pathToPhoto"))
                        .build();
                Congratulation congratulation = Congratulation.builder()
                        .id(congratulationId)
                        .user(userAuthorOfCongratulation)
                        .cardId(card.getId())
                        .message(resultSet.getString("message"))
                        .status(Status.getByNumber(resultSet.getInt("con_status")))
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
        } while (resultSet.next());

        card.setCongratulationList(new ArrayList<>(congratulationMap.values()));
        return card;
    }
}
