package com.greetingcard.dao;

import com.greetingcard.entity.Congratulation;

public interface CongratulationDao {

    Congratulation getCongratulationById(int congratulationId);

    void save(Congratulation congratulation);

    void deleteByCardId(int cardId);
}
