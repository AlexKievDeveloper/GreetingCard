package com.greetingcard.web.servlet.user;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import com.greetingcard.web.servlet.user.EditProfileUserServlet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditProfileUserServletTest {
    @Mock
    private SecurityService service;
    @InjectMocks
    private EditProfileUserServlet servlet;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private ServletContext servletContext;
    @Mock
    private HttpSession session;

}