package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/congratulation")
public class CongratulationController {
    private final CongratulationService congratulationService;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public Congratulation getCongratulation(@PathVariable("id") long congratulationId) {
        log.info("Received request for getting congratulation");
        return congratulationService.getCongratulationById(congratulationId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCongratulation(@RequestParam(required = false) MultipartFile[] files_image,
                                                  @RequestParam(required = false) MultipartFile[] files_audio,
                                                  @RequestParam String json) throws IOException {

        log.info("Received request for saving congratulation");

        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        String jsonString = new String(json.getBytes());

        Map<String, String> parametersMap = objectMapper.readValue(jsonString, typeRef);
        log.info("Got Map from json");
        User user = WebUtils.getCurrentUser();
        long userId = user.getId();
        long cardId = Long.parseLong(parametersMap.get("card_id"));
        log.info("Request for adding congratulation from user: {}", user.getLogin());

        List<Link> linkList = congratulationService.getLinkList(files_image, files_audio, parametersMap);
        log.info("Got linkList: {} and saved files", linkList);

        Congratulation congratulation = Congratulation.builder()
                .message(parametersMap.get("message"))
                .cardId(cardId)
                .user(User.builder().id(userId).build())
                .status(Status.STARTUP)
                .linkList(linkList)
                .build();

        congratulationService.save(congratulation);

        webSocketService.notifyAdmin(user.getLogin() +
                " create congratulation in your card with id: " + cardId, cardId);
        log.info("Successfully created congratulation for user: {}", user.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeCongratulationStatus(@PathVariable("id") long congratulationId) {
        log.info("Received PUT request for congratulation with id: {}", congratulationId);
        congratulationService.changeCongratulationStatusByCongratulationId(Status.ISOVER, congratulationId);

        log.info("Successfully changed status of congratulation with id: {}", congratulationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editCongratulation(@RequestParam(required = false) MultipartFile[] files_image,
                                                @RequestParam(required = false) MultipartFile[] files_audio,
                                                @RequestParam String json,
                                                @PathVariable("id") int congratulationId) throws JsonProcessingException {

        log.info("Received PUT request for edit congratulation with id: {}", congratulationId);
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        long userId = WebUtils.getCurrentUserId();
        Map<String, String> parametersMap = objectMapper.readValue(json, typeRef);
        log.info("Got Map from json");
        congratulationService.updateCongratulationById(files_image, files_audio, parametersMap, congratulationId, userId);
        log.info("Successfully edit congratulation with id: {}", congratulationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCongratulation(@PathVariable("id") long congratulationId) {
        log.info("Request for DELETE congratulation received");
        User user = WebUtils.getCurrentUser();

        log.info("Request DELETE for congratulation with id {}, user: {}", congratulationId, user.getLogin());
        webSocketService.notifyAllCardMembersAboutDeletingCongratulation(congratulationId, user);
        congratulationService.deleteById(congratulationId);
        log.info("Successfully deleted congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/links")
    public ResponseEntity<?> deleteLinksById(@PathVariable("id") long congratulationId,
                                             @RequestBody List<Link> linkIdToDeleteFromCongratulation) {
        log.info("Request for DELETE links received");
        User user = WebUtils.getCurrentUser();

        log.info("Request DELETE links in congratulation with id {}, user: {}", congratulationId, user.getLogin());
        congratulationService.deleteLinksById(linkIdToDeleteFromCongratulation, congratulationId);

        log.info("Successfully deleted links in congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
