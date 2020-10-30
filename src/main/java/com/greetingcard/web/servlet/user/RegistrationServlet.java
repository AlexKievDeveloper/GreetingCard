package com.greetingcard.web.servlet.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.templater.PageGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.greetingcard.web.WebConstants.CONTENT_TYPE;

@Slf4j
public class RegistrationServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("registration request");

        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        Map<String, String> userMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {});

        User user = User.builder()
                .firstName(userMap.get("firstName"))
                .lastName(userMap.get("lastName"))
                .email(userMap.get("email"))
                .login(userMap.get("login"))
                .password(userMap.get("password"))
                .build();
        securityService.save(user);
        response.sendRedirect("/login");
    }
}
