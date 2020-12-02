package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.Congratulation;
import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
import com.greetingcard.entity.User;
import com.greetingcard.service.CongratulationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@RestController
@RequestMapping("/api/v1/congratulation")
public class CongratulationController {
    private CongratulationService congratulationService;
    @Autowired
    private ObjectMapper objectMapper;

    public CongratulationController(CongratulationService congratulationService) {
        this.congratulationService = congratulationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCongratulation(@PathVariable("id") long congratulationId) throws JsonProcessingException {
        log.info("Received request for getting congratulation");
        Congratulation congratulation = congratulationService.getCongratulationById(congratulationId);
        log.info("Successfully returned json with Congratulation entity");
        return ResponseEntity.status(HttpStatus.OK).body(congratulation);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCongratulation(@RequestParam(required = false) MultipartFile[] files_image,
                                                  @RequestParam(required = false) MultipartFile[] files_audio,
                                                  @RequestParam String json,
                                                  HttpSession session) throws IOException {

        log.info("Received request for saving congratulation");

        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        String jsonString = new String(json.getBytes());

        Map<String, String> parametersMap = objectMapper.readValue(jsonString, typeRef);
        log.info("Got Map from json");
        User user = (User) session.getAttribute("user");
        long userId = user.getId();
        log.info("Request for adding congratulation from user: {}", user.getLogin());

        List<Link> linkList = congratulationService.getLinkList(files_image, files_audio, parametersMap);
        log.info("Got linkList: {} and saved files", linkList);

        Congratulation congratulation = Congratulation.builder()
                .message(parametersMap.get("message"))
                .cardId(Long.parseLong(parametersMap.get("card_id")))
                .user(User.builder().id(userId).build())
                .status(Status.STARTUP)
                .linkList(linkList)
                .build();

        congratulationService.save(congratulation);

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
                                                @PathVariable("id") int congratulationId,
                                                HttpSession session) throws JsonProcessingException {

        log.info("Received PUT request for edit congratulation with id: {}", congratulationId);
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        User user = (User) session.getAttribute("user");
        String jsonString = new String(json.getBytes());
        Map<String, String> parametersMap = objectMapper.readValue(jsonString, typeRef);
        log.info("Got Map from json");
        congratulationService.updateCongratulationById(files_image, files_audio, parametersMap, congratulationId, user.getId());
        log.info("Successfully edit congratulation with id: {}", congratulationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCongratulation(@PathVariable("id") long congratulationId, HttpSession session) {
        log.info("Request for DELETE congratulation received");
        User user = (User) session.getAttribute("user");

        log.info("Request DELETE for congratulation with id {}, user: {}", congratulationId, user.getLogin());
        congratulationService.deleteById(congratulationId, user.getId());

        log.info("Successfully deleted congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/links")
    public ResponseEntity<?> deleteLinksById(@PathVariable("id") long congratulationId, HttpSession session,
                                             @RequestBody List<Link> linkIdToDeleteFromCongratulation) {
        log.info("Request for DELETE links received");
        User user = (User) session.getAttribute("user");

        log.info("Request DELETE links in congratulation with id {}, user: {}", congratulationId, user.getLogin());
        congratulationService.deleteLinksById(linkIdToDeleteFromCongratulation, congratulationId);

        log.info("Successfully deleted links in congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
