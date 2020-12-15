package com.greetingcard.web.filter;

import com.greetingcard.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
public class AuthorizationFilter implements Filter {
    private static final Set<String> ALLOWED_PATHS =
            Set.of("/", "/login", "/home", "/api/v1/user/forgot_password", "/signup", "/index.html",
                    "/logo192.png", "/favicon.ico", "/static/*", "/manifest.json", "/api/v1/session", "/api/v1/user",
                    "/api/v1/user/verification/*", "/api/v1/card/*/card_link/*");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String path = httpServletRequest.getRequestURI();

        User user = (User) httpServletRequest.getSession().getAttribute("user");
        boolean allowedPath = isExcludedUrl(path);

        if (user != null || allowedPath) {
            chain.doFilter(request, response);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.info("No user attribute in session. Unauthorized access attempt");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    boolean isExcludedUrl(String url) {
        if (ALLOWED_PATHS.contains("/") && url.isEmpty()) {
            return true;
        }
        for (String allowedPath : ALLOWED_PATHS) {
            if (allowedPath.endsWith("/*")) {
                allowedPath = allowedPath.substring(0, allowedPath.indexOf("*"));
                if (url.startsWith(allowedPath)) {
                    return true;
                }
            } else if (allowedPath.equalsIgnoreCase(url)) {
                return true;
            }

        }
        return false;
    }
}

