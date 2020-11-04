package com.greetingcard.web.servlet.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class RegistrationServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> userMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
                });

        User user = User.builder()
                .firstName(userMap.get("firstName"))
                .lastName(userMap.get("lastName"))
                .email(userMap.get("email"))
                .login(userMap.get("login"))
                .password(userMap.get("password"))
                .build();

        log.info("Registration request for user login: {}", user.getLogin());

        try {
            securityService.save(user);
            response.setStatus(HttpServletResponse.SC_CREATED);
            log.info("Successfully registered: {}", user);
        } catch (RuntimeException e) {
            response.getWriter().print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Exception while registration user login: {}", user.getLogin());
        }
    }
}
