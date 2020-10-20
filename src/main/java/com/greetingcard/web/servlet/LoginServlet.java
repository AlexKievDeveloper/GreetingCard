package com.greetingcard.web.servlet;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.util.PropertyReader;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.greetingcard.web.WebConstants.CONTENT_TYPE;

public class LoginServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");
    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private int maxInactiveInterval = Integer.parseInt(propertyReader.getProperty("max.inactive.interval"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        response.setContentType(CONTENT_TYPE);
        parameters.put("message", request.getParameter("message"));
        PageGenerator.getInstance().process("login", parameters, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        User user = securityService.login(login, password);
        if (user != null) {
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);
            httpSession.setMaxInactiveInterval(maxInactiveInterval);
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/login?message=Access denied. Please login and try again.");
        }
    }
}

