package com.greetingcard.web.servlet.congratulation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.*;
import com.greetingcard.service.CongratulationService;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddCongratulationServlet extends HttpServlet {
    private CongratulationService congratulationService = ServiceLocator.getBean("DefaultCongratulationService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageGenerator.getInstance().process("add-congratulation", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getId();
        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);

        Map<String, String> parametersMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {});

        String message = parametersMap.get("message");
        int cardId = Integer.parseInt(parametersMap.get("card-id"));

        List<Part> partList = new ArrayList<>(request.getParts());
        List<Link> linkList = congratulationService.getLinkList(partList, request);

        Congratulation congratulation = Congratulation.builder()
                .message(message)
                .card(Card.builder().id(cardId).build())
                .user(User.builder().id(userId).build())
                .status(Status.STARTUP)
                .linkList(linkList)
                .build();

        congratulationService.save(congratulation);
    }
}
