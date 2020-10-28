package com.greetingcard.service;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.List;

public interface CongratulationService {

    Congratulation getCongratulationById(int congratulationId);

    List<Link> getLinkList(List<Part> partList, HttpServletRequest request);

    void save(Congratulation congratulation);
}