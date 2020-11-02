package com.greetingcard.web.servlet.congratulation;

import com.greetingcard.entity.Link;
import com.greetingcard.entity.User;
import com.greetingcard.service.CongratulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddCongratulationServletTest {
    @Mock
    private Part part;
    @Mock
    private User user;
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    @Mock
    private ServletContext context;
    @Mock
    private List<Link> linkList;
    @Mock
    private CongratulationService congratulationService;
    @InjectMocks
    private AddCongratulationServlet addCongratulationServlet;

    @Test
    @DisplayName("Returns add congratulation form")
    void doGetTest() throws IOException {
        //prepare
        when(request.getServletContext()).thenReturn(context);
        when(response.getWriter()).thenReturn(writer);
        //when
        addCongratulationServlet.doGet(request, response);
        //then
        verify(request).getServletContext();
        verify(request).getLocale();
        verify(response).getWriter();
    }

    @Test
    @DisplayName("Saving congratulation to DB")
    void doPostTest() throws IOException, ServletException {
        //prepare
        Collection<Part> parts = new ArrayList<>();
        parts.add(part);
        when(request.getParts()).thenReturn(parts);
        when(congratulationService.getLinkList(any(), any())).thenReturn(linkList);
        when(congratulationService.getLinkList(any(), any(), any())).thenReturn(linkList);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(request.getParameter("message")).thenReturn("message");
        when(request.getParameter("card-id")).thenReturn("1");
        when(request.getParameter("youtube")).thenReturn("https://www.youtube.com/watch?v=pCdCHqCUsZg");
        when(request.getParameter("plain-link")).thenReturn("https://www.studytonight.com/servlet/httpsession.php");

        //when
        addCongratulationServlet.doPost(request, response);
        //then
        verify(request).getParts();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(request).getParameter("youtube");
        verify(request).getParameter("plain-link");
        verify(request).getParameter("message");
        verify(request).getParameter("card-id");
    }
}