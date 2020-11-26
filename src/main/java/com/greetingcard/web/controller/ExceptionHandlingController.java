package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Map.of("message", e.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> serverError(RuntimeException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Map.of("message", e.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<?> parsingJson() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
