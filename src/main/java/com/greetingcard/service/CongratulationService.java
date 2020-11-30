package com.greetingcard.service;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CongratulationService {

    Congratulation getCongratulationById(int congratulationId);

    List<Link> getLinkList(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String, String> parametersMap);

    void save(Congratulation congratulation);

    void changeCongratulationStatusByCongratulationId(Status status, long congratulationId);

    void deleteById(long congratulationId, long userId);

    void deleteByCardId(long cardId, long userId);
}