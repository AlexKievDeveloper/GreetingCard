package com.greetingcard.web.servlet.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.util.PropertyReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginLogoutServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");
    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private int maxInactiveInterval = Integer.parseInt(propertyReader.getProperty("max.inactive.interval"));

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        // response.sendRedirect("/login-logout");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] bytes = request.getInputStream().readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);

        Map<String, String> loginPasswordMap =
                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {});

        String login = loginPasswordMap.get("login");
        String password = loginPasswordMap.get("password");

        User user = securityService.login(login, password);
        if (user != null) {
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            httpSession.setMaxInactiveInterval(maxInactiveInterval);
            response.sendRedirect("/all-cards?cards-type=All-cards");
        } else {
            Map<String, String> messageMap = new LinkedHashMap<>();
            messageMap.put("message", "Access denied. Please login and try again.");
            response.getWriter().print(JSON.toJSONString(messageMap));
        }
    }
}

