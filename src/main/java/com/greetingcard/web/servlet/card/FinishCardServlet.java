package com.greetingcard.web.servlet.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Status;
import com.greetingcard.service.CardService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class FinishCardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Received PUT request");
        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> parametersMap = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
        });

        long cardId = Long.parseLong(parametersMap.get("id"));
        log.info("Received PUT request for card id: {}", cardId);

        try {
            cardService.changeCardStatus(Status.ISOVER, cardId);
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while changing card status with card id: {}", cardId);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("Successfully changed card status for card id: {}", cardId);
    }
}
