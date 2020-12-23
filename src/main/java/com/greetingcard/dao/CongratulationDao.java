package com.greetingcard.dao;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;

import java.util.List;

public interface CongratulationDao {

    Congratulation getCongratulationById(long congratulationId);

    void save(Congratulation congratulation);

    void deleteByCardId(long cardId, long userId);

    List<Congratulation> findCongratulationsByCardId(long cardId);

    void changeCongratulationsStatusByCardId(Status status, long cardId);

    void changeCongratulationStatusByCongratulationId(Status status, long congratulationId);

    void updateCongratulationMessage(String message, long congratulationId, long userId);

    void deleteById(long congratulationId, long userId);

    void saveLinks(List<Link> linkList, long congratulationId);

    void deleteLinksById(List<Link> linkIdToDelete, long congratulationId);
}
