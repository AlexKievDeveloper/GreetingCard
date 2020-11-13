package com.greetingcard.web.servlet.congratulation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.*;
import com.greetingcard.service.CongratulationService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CongratulationServlet extends HttpServlet {
    private CongratulationService congratulationService = ServiceLocator.getBean("DefaultCongratulationService");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getId();
        log.info("Received POST request for adding congratulation from user: {}", user.getLogin());

        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> parametersMap = JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
        });

        String message = parametersMap.get("message");
        int cardId = Integer.parseInt(parametersMap.get("card_id"));
        String youtubeLinks = parametersMap.get("youtube");
        String plainLinks = parametersMap.get("plain_link");

        List<Link> linkList = congratulationService.getLinkList(youtubeLinks, plainLinks);

        Congratulation congratulation = Congratulation.builder()
                .message(message)
                .card(Card.builder().id(cardId).build())
                .user(User.builder().id(userId).build())
                .status(Status.STARTUP)
                .linkList(linkList)
                .build();
        try {
            congratulationService.save(congratulation);
            response.setStatus(HttpServletResponse.SC_CREATED);
            log.info("Successfully creating congratulation for user: {}", user.getLogin());
        } catch (RuntimeException e) {
            response.getWriter().println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while creating congratulation for user: {}", user.getLogin());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Received PUT request");

        String[] path = request.getPathInfo().split("/");
        long congratulationId = Long.parseLong(path[path.length - 2]);

        log.info("Received PUT request for congratulation id: {}", congratulationId);

        try {
            congratulationService.changeCongratulationStatusByCongratulationId(Status.ISOVER, congratulationId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            log.info("Successfully changed congratulation status for congratulation id: {}", congratulationId);
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while changing congratulation status with congratulation id: {}", congratulationId);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        log.info("Request for DELETE congratulation");
        User user = (User) request.getSession().getAttribute("user");
        String[] path = request.getPathInfo().split("/");
        long congratulationId = Long.parseLong(path[path.length - 1]);

        log.info("Request DELETE for congratulation with id {}, user: {}", congratulationId, user.getLogin());

        try {
            congratulationService.deleteById(congratulationId, user.getId());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            log.info("Successfully deleted congratulation with id: {}, user login: {}", congratulationId, user.getLogin());
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while deleting congratulation id: {}, user login: {}", congratulationId, user.getLogin());
        }
    }
}
