package com.greetingcard.web.servlet.card;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
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
public class CardServlet extends HttpServlet {
    private CardService cardService = ServiceLocator.getBean("DefaultCardService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Get card request");

        String[] path = request.getPathInfo().split("/");
        long id = Long.parseLong(path[path.length - 1]);

        log.info("Received GET request for card id: {}", id);

        User user = (User) request.getSession().getAttribute("user");
        Card card = null;
        try {
            card = cardService.getCardAndCongratulationByCardId(id, user.getId());
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while getting card: {}, user login: {}", id, user.getLogin());
            return;
        }
        String json = JSON.toJSONString(card);
        response.getWriter().print(json);
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("Successfully writing card to response, id: {}", id);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Creating card request");

        User user = (User) request.getSession().getAttribute("user");
        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> nameOfCard = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
        });
        String name = nameOfCard.get("name");

        log.info("Received POST request for creating card name: {}, user login: {}", name, user.getLogin());

        Card card = Card.builder().user(user).name(name).build();

        try {
            long cardId = cardService.createCard(card);
            Map<String, Long> parametersMap = new LinkedHashMap<>();
            parametersMap.put("id", cardId);
            String jsonForResponse = JSON.toJSONString(parametersMap);
            response.getWriter().print(jsonForResponse);
            log.info("Successfully created card name: {}, id: {}", name, cardId);
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while creating card: {}, user login: {}", name, user.getLogin());
            return;
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        log.info("Request for DELETE card");
        User user = (User) request.getSession().getAttribute("user");
        String[] path = request.getPathInfo().split("/");
        long cardId = Long.parseLong(path[path.length - 1]);

        log.info("Request DELETE for card with id {}, user: {}", cardId, user.getLogin());

        try {
            cardService.deleteCardById(cardId, user.getId());
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while deleting card id: {}, user login: {}", cardId, user.getLogin());
            return;
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        log.info("Successfully deleted card with id: {}, user login: {}", cardId, user.getLogin());
    }
}
