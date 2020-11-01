package com.greetingcard.web.servlet.congratulation;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.*;
import com.greetingcard.service.CongratulationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddCongratulationServlet extends HttpServlet {
    private CongratulationService congratulationService = ServiceLocator.getBean("DefaultCongratulationService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Collection<Part> partList = new ArrayList<>(request.getParts());
        String youtubeLinks = request.getParameter("youtube");
        String plainLinks = request.getParameter("plain-link");
        List<Link> linkList = congratulationService.getLinkList(partList, youtubeLinks, plainLinks);

        User user = (User) request.getSession().getAttribute("user");
        long userId = user.getId();
        String message = request.getParameter("message");
        int cardId = Integer.parseInt(request.getParameter("card-id"));

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
