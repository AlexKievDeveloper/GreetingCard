package com.greetingcard.dao;

import com.greetingcard.entity.Congratulation;

public interface CongratulationDao {

    void save(Congratulation congratulation);

    Congratulation getCongratulationById(int congratulationId);
}
