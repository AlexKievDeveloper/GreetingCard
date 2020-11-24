package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.*;
import com.greetingcard.service.CongratulationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Setter
@RestController
@RequestMapping(value = "/api/v1/congratulation")
public class CongratulationController {
    private CongratulationService congratulationService;

    public CongratulationController(CongratulationService congratulationService) {
        this.congratulationService = congratulationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCongratulation(@RequestParam MultipartFile[] files_image, @RequestParam MultipartFile[] files_audio,
                                                  HttpSession session, @RequestParam String json) throws JsonProcessingException {

        log.info("Received request for saving congratulation");
        log.info("Image files: :" + files_image.length);
        log.info("Audio files: :" + files_audio.length);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };

        HashMap<String, String> parametersMap = objectMapper.readValue(json, typeRef);
        log.info("Got Map from json");
        User user = (User) session.getAttribute("user");
        long userId = user.getId();
        log.info("Request for adding congratulation from user: {}", user.getLogin());

        List<Link> linkList = congratulationService.getLinkList(files_image, files_audio, parametersMap);
        log.info("Got linkList and saved files");
        Congratulation congratulation = Congratulation.builder()
                .message(parametersMap.get("message"))
                .card(Card.builder().id(Integer.parseInt(parametersMap.get("card_id"))).build())
                .user(User.builder().id(userId).build())
                .status(Status.STARTUP)
                .linkList(linkList)
                .build();

        congratulationService.save(congratulation);

        log.info("Successfully created congratulation for user: {}", user.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{id}/status")
    public ResponseEntity<?> changeCongratulationStatus(@PathVariable("id") int congratulationId) {
        log.info("Received PUT request for congratulation with id: {}", congratulationId);
        congratulationService.changeCongratulationStatusByCongratulationId(Status.ISOVER, congratulationId);

        log.info("Successfully changed status of congratulation with id: {}", congratulationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCongratulation(@PathVariable("id") int congratulationId, HttpSession session) {
        log.info("Request for DELETE congratulation received");
        User user = (User) session.getAttribute("user");

        log.info("Request DELETE for congratulation with id {}, user: {}", congratulationId, user.getLogin());
        congratulationService.deleteById(congratulationId, user.getId());

        log.info("Successfully deleted congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
