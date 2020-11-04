package com.greetingcard.web.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class СORSFilterTest {

    @InjectMocks
    CORSFilter corsFilter;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private FilterChain filterChain;

    @Test
    void doFilter() throws IOException, ServletException {
        when(servletRequest.getMethod()).thenReturn("OPTIONS");

        corsFilter.doFilter(servletRequest,servletResponse,filterChain);

        verify(servletResponse).addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        verify(servletResponse).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");
        verify(servletResponse).addHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        verify(servletResponse).addHeader("Access-Control-Allow-Credentials", "true");
        verify(servletResponse).setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
