package com.greetingcard.dao.jdbc.mapper;

import com.greetingcard.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CardAndCongratulationRowMapperTest {
//                if (card == null) {
//        card = Card.builder()
//                .id((resultSet.getInt("card_id")))
//                .name(resultSet.getString("name"))
//                .backgroundImage(resultSet.getString("background_image"))
//                .cardLink(resultSet.getString("card_link"))
//                .status(Status.getByNumber(resultSet.getInt("status_id")))
//                .congratulationList(congratulationList)
//                .build();
//    }
//    int congratulation_id = resultSet.getInt("congratulation_id");
//            if (congratulation_id != 0 && !congratulationMap.containsKey(congratulation_id)) {
//        User user = User.builder()
//                .id(resultSet.getInt("user_id"))
//                .firstName(resultSet.getString("firstName"))
//                .lastName(resultSet.getString("lastName"))
//                .login(resultSet.getString("login"))
//                .build();
//        Congratulation congratulation = Congratulation.builder()
//                .id(congratulation_id)
//                .user(user)
//                .card(card)
//                .message(resultSet.getString("message"))
//                .status(Status.getByNumber(resultSet.getInt("con_status")))
//                .linkList(new LinkedList<>())
//                .build();
//
//        congratulationMap.put(congratulation_id, congratulation);
//int linkId = resultSet.getInt("link_id");
//        if (linkId != 0) {
//        Link link = Link.builder()
//                .id(linkId)
//                .link(resultSet.getString("message"))
//                .type(LinkType.getByNumber(resultSet.getInt("type_id")))
//                .build();
//        congratulationMap.get(congratulation_id).getLinkList().add(link);
//    }
//        if (card!=null){
//        card.setCongratulationList(new ArrayList<>(congratulationMap.values()));
//    }

    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Returns an object of class Card with all congratulations from result set")
    void mapRow() throws SQLException {
        //prepare
        CardAndCongratulationRowMapper rowMapper = new CardAndCongratulationRowMapper();

        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("card_id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Card");
        when(mockResultSet.getString("background_image")).thenReturn("/link");
        when(mockResultSet.getString("card_link")).thenReturn("/link");
        when(mockResultSet.getInt("status_id")).thenReturn(1);

        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("firstName")).thenReturn("user");
        when(mockResultSet.getString("lastName")).thenReturn("user");
        when(mockResultSet.getString("login")).thenReturn("user");

        when(mockResultSet.getInt("congratulation_id")).thenReturn(1);
        when(mockResultSet.getString("message")).thenReturn("from Roma");
        when(mockResultSet.getInt("card_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("con_status")).thenReturn(1);

        when(mockResultSet.getInt("link_id")).thenReturn(1);
        when(mockResultSet.getString("link")).thenReturn("/link");
        when(mockResultSet.getInt("type_id")).thenReturn(1);

        //when
        Card actualCard = rowMapper.mapRow(mockResultSet);

        //then
        verify(mockResultSet).getInt("card_id");
        verify(mockResultSet).getString("name");
        verify(mockResultSet).getString("background_image");
        verify(mockResultSet).getString("card_link");
        verify(mockResultSet).getInt("status_id");

        verify(mockResultSet).getInt("user_id");
        verify(mockResultSet).getString("firstName");
        verify(mockResultSet).getString("lastName");
        verify(mockResultSet).getString("login");

        verify(mockResultSet).getInt("congratulation_id");
        verify(mockResultSet).getString("message");
        verify(mockResultSet).getInt("card_id");
        verify(mockResultSet).getInt("user_id");
        verify(mockResultSet).getInt("con_status");

        verify(mockResultSet).getInt("link_id");
        verify(mockResultSet).getString("link");
        verify(mockResultSet).getInt("type_id");
    }
}