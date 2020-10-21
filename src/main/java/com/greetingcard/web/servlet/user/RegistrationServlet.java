package com.greetingcard.web.servlet.user;

import com.greetingcard.ServiceLocator;
import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.greetingcard.web.WebConstants.CONTENT_TYPE;

public class RegistrationServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        PageGenerator.getInstance().process("registration", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = User.builder()
                .firstName(request.getParameter("firstName"))
                .lastName(request.getParameter("lastName"))
                .email(request.getParameter("email"))
                .login(request.getParameter("login"))
                .password(request.getParameter("password")).build();
        securityService.save(user);
        response.sendRedirect("/login");
    }
}
