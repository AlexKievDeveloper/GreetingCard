package com.greetingcard.web.servlet;

import com.greetingcard.ServiceLocator;
import com.greetingcard.security.SecurityService;
import com.greetingcard.security.Session;
import com.greetingcard.util.PropertyReader;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.greetingcard.web.WebConstants.CONTENT_TYPE;

public class LoginServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");
    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
    private int sessionMaxAge = Integer.parseInt(propertyReader.getProperty("session.max-age"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = new HashMap();
        response.setContentType(CONTENT_TYPE);
        parameters.put("message", request.getParameter("message"));
        PageGenerator.instance().process("login", parameters, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        Session session = securityService.login(login, password);
        if (session != null) {
            Cookie cookie = new Cookie("user-token", session.getToken());
            cookie.setMaxAge(sessionMaxAge);
            response.addCookie(cookie);
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/login?message=Access denied. Please login and try again.");
        }
    }
}
