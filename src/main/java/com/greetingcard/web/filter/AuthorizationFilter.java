package com.greetingcard.web.filter;

import com.greetingcard.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        User user = (User) httpServletRequest.getSession().getAttribute("user");

        if (user != null) {
            chain.doFilter(request, response);
        } else {
            httpServletResponse.sendRedirect("/api/v1/session");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}

