package com.greetingcard.service;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;

import java.util.List;

public interface CongratulationService {

    Congratulation getCongratulationById(int congratulationId);

    List<Link> getLinkList(String youtubeLinks, String plainLinks);

    void save(Congratulation congratulation);

    void changeCongratulationStatusByCongratulationId(Status status, long congratulationId);

    void deleteById(long congratulationId, long userId);
}