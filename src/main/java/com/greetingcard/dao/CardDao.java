package com.greetingcard.dao;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;

import java.util.Map;

public interface CardDao {
    Map<Card, Role> getAllCardsByUserId(int id);

    void createCard(Card card, User user);

}
