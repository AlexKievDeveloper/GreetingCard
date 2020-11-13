package com.greetingcard.web.servlet.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginLogoutServletITest {
//    @Mock
//    private HttpServletRequest request;
//    @Mock
//    private HttpServletResponse response;
//    @Mock
//    private HttpSession httpsession;
//    @Mock
//    private ServletInputStream inputStream;
//    @Mock
//    private PrintWriter printWriter;
//    @Mock
//    private SecurityService securityService;
//    @InjectMocks
//    private LoginLogoutServlet loginLogoutServlet;
//
//    @Test
//    @DisplayName("Invalidates the session")
//    void doDelete() {
//        //prepare
//        LoginLogoutServlet loginLogoutServlet = new LoginLogoutServlet();
//        when(request.getSession()).thenReturn(httpsession);
//        //when
//        loginLogoutServlet.doDelete(request, response);
//        //then
//        verify(request).getSession();
//        verify(httpsession).invalidate();
//    }
//
//    @Test
//    @DisplayName("Login user and set User as session attribute ")
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    void doPostTestUserExist() throws IOException {
//        //prepare
//        byte[] bytes = "{\"login\":\"user\",\"password\":\"user\"}".getBytes();
//        User user = User.builder().build();
//        when(request.getInputStream()).thenReturn(inputStream);
//        when(inputStream.readAllBytes()).thenReturn(bytes);
//        when(response.getWriter()).thenReturn(printWriter);
//        when(securityService.login("user", "user")).thenReturn(user);
//        when(request.getSession()).thenReturn(httpsession);
//        doNothing().when(httpsession).setAttribute("user", user);
//        doNothing().when(httpsession).setMaxInactiveInterval(anyInt());
//        //when
//        loginLogoutServlet.doPost(request, response);
//        //then
//        verify(request).getInputStream();
//        verify(inputStream).readAllBytes();
//        verify(securityService).login("user", "user");
//        verify(request).getSession();
//        verify(httpsession).setAttribute("user", user);
//        verify(httpsession).setMaxInactiveInterval(anyInt());
//        verify(response).setStatus(HttpServletResponse.SC_OK);
//    }
//
//    @Test
//    @DisplayName("Returns message: Access denied. Please login and try again.")
//    @MockitoSettings(strictness = Strictness.LENIENT)
//    void doPostTestUserIsNull() throws IOException {
//        //prepare
//        byte[] bytes = "{\"login\":\"noUser\",\"password\":\"noUser\"}".getBytes();
//        when(request.getInputStream()).thenReturn(inputStream);
//        when(inputStream.readAllBytes()).thenReturn(bytes);
//        when(response.getWriter()).thenReturn(printWriter);
//        when(securityService.login("noUser", "noUser")).thenReturn(null);
//        when(request.getSession()).thenReturn(httpsession);
//        doNothing().when(httpsession).setAttribute(anyString(), any());
//        doNothing().when(httpsession).setMaxInactiveInterval(anyInt());
//        //when
//        loginLogoutServlet.doPost(request, response);
//        //then
//        verify(request).getInputStream();
//        verify(inputStream).readAllBytes();
//        verify(response).getWriter();
//        verify(printWriter).print("{\"message\":\"Access denied. Please login and try again.\"}");
//        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//    }
}
