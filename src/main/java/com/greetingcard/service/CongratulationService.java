package com.greetingcard.service;

import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CongratulationService {
    Congratulation getCongratulationById(long congratulationId);

    List<Link> getLinkList(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String, String> parametersMap);

    void save(Congratulation congratulation);

    void changeCongratulationStatusByCongratulationId(Status status, long congratulationId);

    void changeCongratulationStatusByCardId(Status status, long cardId);

    void updateCongratulationById(MultipartFile[] files_image, MultipartFile[] files_audio, Map<String, String> parametersMap, long congratulationId, long userId);

    void deleteById(long congratulationId);

    void deleteLinksById(List<Link> linkIdToDelete, long congratulationId);

    void deleteByCardId(long cardId, long userId);
}