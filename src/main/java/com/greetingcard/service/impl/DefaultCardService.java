package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.JdbcCardDao;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.Role;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;

import java.util.Map;


public class DefaultCardService implements CardService {
    private final JdbcCardDao jdbcCardDao;

    public DefaultCardService(JdbcCardDao jdbcCardDao) {
        this.jdbcCardDao = jdbcCardDao;
    }

    @Override
    public Map<Card, Role> getAllCardsByUserId(int id) {
        return jdbcCardDao.getAllCardsByUserId(id);
    }

    @Override
    public void createCard(Card card, User user) {
        jdbcCardDao.createCard(card, user);
    }
}

