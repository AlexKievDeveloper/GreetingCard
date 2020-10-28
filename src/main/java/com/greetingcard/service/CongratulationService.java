package com.greetingcard.service;

import com.greetingcard.entity.Congratulation;

public interface CongratulationService {

    Congratulation getCongratulationById(int congratulationId);

    void save(Congratulation congratulation);

    void leaveByCardId(long cardId, long userId);
}
