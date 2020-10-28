package com.greetingcard.dao;

import com.greetingcard.entity.Congratulation;

public interface CongratulationDao {

    Congratulation getCongratulationById(int congratulationId);

    void save(Congratulation congratulation);

    void leaveByCardId(long cardId, long userId);
}