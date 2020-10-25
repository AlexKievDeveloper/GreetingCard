package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.service.CongratulationService;

public class DefaultCongratulationService implements CongratulationService {
    private final JdbcCongratulationDao jdbcCongratulationDao;

    public DefaultCongratulationService(JdbcCongratulationDao jdbcCongratulationDao) {
        this.jdbcCongratulationDao = jdbcCongratulationDao;
    }

    @Override
    public Congratulation getCongratulationById(int congratulationId) {
        return jdbcCongratulationDao.getCongratulationById(congratulationId);
    }

    @Override
    public void save(Congratulation congratulation) {
        jdbcCongratulationDao.save(congratulation);
    }

    @Override
    public void deleteByCardId(int cardId) {
        jdbcCongratulationDao.deleteByCardId(cardId);
    }
}
