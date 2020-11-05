package com.greetingcard.web.servlet.user;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginLogoutServletITest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession httpsession;
    @Mock
    private ServletInputStream inputStream;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private LoginLogoutServlet loginLogoutServlet;

    @Test
    @DisplayName("Invalidates the session")
    void doDelete() {
        //prepare
        LoginLogoutServlet loginLogoutServlet = new LoginLogoutServlet();
        when(request.getSession()).thenReturn(httpsession);
        //when
        loginLogoutServlet.doDelete(request, response);
        //then
        verify(request).getSession();
        verify(httpsession).invalidate();
    }

    @Test
    @DisplayName("Login user and set User as session attribute ")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void doPostTestUserExist() throws IOException {
        //prepare
        byte[] bytes = "{\"login\":\"user\",\"password\":\"user\"}".getBytes();
        User user = User.builder().build();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(response.getWriter()).thenReturn(printWriter);
        when(securityService.login("user", "user")).thenReturn(user);
        when(request.getSession()).thenReturn(httpsession);
        doNothing().when(httpsession).setAttribute("user", user);
        doNothing().when(httpsession).setMaxInactiveInterval(anyInt());
        //when
        loginLogoutServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(securityService).login("user", "user");
        verify(request).getSession();
        verify(httpsession).setAttribute("user", user);
        verify(httpsession).setMaxInactiveInterval(anyInt());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Returns message: Access denied. Please login and try again.")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void doPostTestUserIsNull() throws IOException {
        //prepare
        byte[] bytes = "{\"login\":\"noUser\",\"password\":\"noUser\"}".getBytes();
        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
        when(response.getWriter()).thenReturn(printWriter);
        when(securityService.login("noUser", "noUser")).thenReturn(null);
        when(request.getSession()).thenReturn(httpsession);
        doNothing().when(httpsession).setAttribute(anyString(), any());
        doNothing().when(httpsession).setMaxInactiveInterval(anyInt());
        //when
        loginLogoutServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(inputStream).readAllBytes();
        verify(response).getWriter();
        verify(printWriter).print("{\"message\":\"Access denied. Please login and try again.\"}");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
