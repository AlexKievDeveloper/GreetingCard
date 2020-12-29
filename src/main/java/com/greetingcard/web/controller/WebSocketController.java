package com.greetingcard.web.controller;

import com.greetingcard.entity.WebRequest;
import com.greetingcard.entity.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {
    @MessageMapping("/request")
    @SendTo("/topic/greetings")
    public WebResponse webResponseUser(WebRequest webRequest) throws InterruptedException {
        log.info("I received message from react: " + webRequest.getMessage());
        Thread.sleep(1000);
        WebResponse webResponse = new WebResponse("Server received message: " +
                webRequest.getMessage() + ". connection established.");
        return webResponse;
    }
}
