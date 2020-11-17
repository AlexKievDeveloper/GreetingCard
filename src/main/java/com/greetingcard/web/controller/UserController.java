package com.greetingcard.web.controller;

import com.greetingcard.dto.UserCredential;
import com.greetingcard.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/v1/session")
public class UserController {

    private SecurityService securityService;

    @DeleteMapping
    public void logout(@RequestAttribute HttpSession session, HttpServletResponse response) {
        log.info("logout");
        session.invalidate();
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        log.info("Successfully logout");
    }

    @PostMapping
    public void login(@RequestBody UserCredential userCredential) {
        log.info("login request");
        String login = userCredential.getLogin();
        String password = userCredential.getPassword();
        log.info("login for user {}", login);

//        try {
//            User user = securityService.login(login, password);
//            if (user != null) {
//                HttpSession httpSession = request.getSession();
//                httpSession.setAttribute("user", user);
//                httpSession.setMaxInactiveInterval(maxInactiveInterval);
//                response.setStatus(HttpServletResponse.SC_OK);
//                log.info("Successfully login");
//            } else {
//                Map<String, String> messageMap = new LinkedHashMap<>();
//                messageMap.put("message", "Access denied. Please login and try again.");
//                response.getWriter().print(JSON.toJSONString(messageMap));
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                log.info("Credentials not valid");
//            }
//        } catch (RuntimeException e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            log.error("Exception while checking credentials");
//        }
    }
}
