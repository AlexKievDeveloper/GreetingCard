package com.greetingcard.dao;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Status;

import java.util.List;

public interface CongratulationDao {

    Congratulation getCongratulationById(int congratulationId);

    void save(Congratulation congratulation);

    void deleteByCardId(long cardId, long userId);

    List<Congratulation> findCongratulationsByCardId(long cardId);

    void changeStatusCongratulationsByCardId(Status status, long cardId);

    void deleteById(long congratulationId, long userId);
}
