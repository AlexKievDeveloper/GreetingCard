package com.greetingcard.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greetingcard.entity.WebRequest;
import com.greetingcard.entity.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
/*@Rest*/@Controller
public class WebSocketController {
/*   @Autowired
    ObjectMapper objectMapper;

    @MessageMapping("/request")
    @SendTo("/topic/20")
    public WebResponse ResponseEntity<?>webResponseUser(WebRequest webRequest) throws InterruptedException, JsonProcessingException {
        log.info("I received message from react: " + webRequest.getMessage());
        Thread.sleep(1000);
        WebResponse webResponse = new WebResponse("Hello React. I am server. I received your message: " +
                webRequest.getMessage() + ". Of course, lets connect!");
        String json = objectMapper.writeValueAsString(webResponse);
        return ResponseEntity.status(HttpStatus.OK).body(json);
        //return new WebResponse("Hello React. I am server. I received your message: " + webRequest.getMessage());
    }*/

    @MessageMapping("/request")
    @SendTo("/topic/20")
    public WebResponse webResponseUser(WebRequest webRequest) throws InterruptedException {
        log.info("I received message from react: " + webRequest.getMessage());
        Thread.sleep(1000);
        WebResponse webResponse = new WebResponse("Hello React. I am server. I received your message: " +
                webRequest.getMessage() + ". Of course, lets connect!");
        return webResponse;
    }
}













/*     @Autowired
    private SimpMessagingTemplate template;*/

/*    @MessageMapping("/request")
    @SendTo("/topic/greetings")
    public WebResponse*//*ResponseEntity<?> *//*webResponse(WebRequest webRequest) throws InterruptedException, JsonProcessingException {
        //log.info("I received message from react: " + webRequest.getMessage());
        Thread.sleep(1000);
*//*        WebResponse webResponse = new WebResponse("Hello React. I am server. I received your message: " +
                webRequest.getMessage() + ". Of course, lets connect!");
        String json = objectMapper.writeValueAsString(webResponse);
        return ResponseEntity.status(HttpStatus.OK).body(json);*//*
        return new WebResponse("Hello React. I am server. I received your message: " + webRequest.getMessage());
    }*/

    /*//Реакт отправил инфу о том что человек нажал на кнопку к примеру завершение карты,
    сервер словил запрос, обработал и выдал об этом сообщение в основной канал
    Или реакт отправил запрос в контроллер с логикой который походу выполнения логики отправит сообщение в
    основной канал
    */
/*    @RequestMapping("/sendMessage")
    public void sendMessage() {
        this.template.convertAndSend("/topic/greetings", new WebResponse("This is Send From Server"));
    }*/


/*, produces = MediaType.APPLICATION_JSON_VALUE*/
/**/
/*@ResponseBody
ResponseEntity<?>

  @Autowired
  ObjectMapper objectMapper;
  WebResponse webResponse = new WebResponse("Hello React. I am server. I received your message: " + webRequest.getMessage());
  String json = objectMapper.writeValueAsString(webResponse);
  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(json);
*/