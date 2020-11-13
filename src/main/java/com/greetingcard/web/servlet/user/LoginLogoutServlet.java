package com.greetingcard.web.servlet.user;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;

@Slf4j
public class LoginLogoutServlet extends HttpServlet {
//    private SecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");
//    private PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
//    private int maxInactiveInterval = Integer.parseInt(propertyReader.getProperty("max.inactive.interval"));
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
//        log.info("logout");
//        request.getSession().invalidate();
//        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//        log.info("Successfully logout");
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        log.info("login request");
//
//        byte[] bytes = request.getInputStream().readAllBytes();
//        String json = new String(bytes, StandardCharsets.UTF_8);
//        Map<String, String> loginPasswordMap =
//                JSON.parseObject(json, new TypeReference<LinkedHashMap<String, String>>() {
//                });
//
//        String login = loginPasswordMap.get("login");
//        String password = loginPasswordMap.get("password");
//        log.info("login for user {}", login);
//
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
//    }
}