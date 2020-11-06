package com.greetingcard.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private int maxInactiveInterval;


    @RequestMapping(value = "/session", method = RequestMethod.DELETE)
    public void logout(@RequestAttribute HttpSession session, HttpServletResponse response) {
        log.info("logout");
        session.invalidate();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        log.info("Successfully logout");
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("login request");

        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> loginPasswordMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
                });

        String login = loginPasswordMap.get("login");
        String password = loginPasswordMap.get("password");
        log.info("login for user {}", login);

        try {
            User user = securityService.login(login, password);
            if (user != null) {
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("user", user);
                httpSession.setMaxInactiveInterval(maxInactiveInterval);
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Successfully login");
            } else {
                Map<String, String> messageMap = new LinkedHashMap<>();
                messageMap.put("message", "Access denied. Please login and try again.");
                response.getWriter().print(JSON.toJSONString(messageMap));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.info("Credentials not valid");
            }
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while checking credentials");
        }
    }
}
