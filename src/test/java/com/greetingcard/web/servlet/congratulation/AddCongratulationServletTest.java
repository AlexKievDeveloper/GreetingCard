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

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddCongratulationServletTest {
    @Mock
    private ServletInputStream servletInputStream;
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
    private List<Link> linkList;
    @Mock
    private CongratulationService congratulationService;
    @Mock
    private PrintWriter printWriter;
    @InjectMocks
    private AddCongratulationServlet addCongratulationServlet;

    @Test
    @DisplayName("Saving congratulation to DB")
    void doPostTest() throws IOException, ServletException {
        //prepare
        Collection<Part> parts = new ArrayList<>();
        parts.add(part);
        String json = "{\n" +
                "  \"youtube\" : \"https://www.youtube.com/watch?v=r-0qNVT_I4s\",\n" +
                "  \"plain_link\" : \"https://www.studytonight.com/servlet/httpsession.php\",\n" +
                "  \"message\" : \"Happy Birthday\",\n" +
                "  \"card_id\" : \"1\",\n" +
                "}";
        byte[] bytes = json.getBytes();

        when(request.getParts()).thenReturn(parts);
        when(congratulationService.getLinkList(any(), anyString(), anyString())).thenReturn(linkList);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(bytes);
        //when
        addCongratulationServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(request).getParts();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(congratulationService).save(any());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Exception while saving congratulation to DB")
    void doPostExceptionTest() throws IOException, ServletException {
        //prepare
        Collection<Part> parts = new ArrayList<>();
        parts.add(part);
        String json = "{\n" +
                "  \"youtube\" : \"https://www.youtube.com/watch?v=r-0qNVT_I4s\",\n" +
                "  \"plain_link\" : \"https://www.studytonight.com/servlet/httpsession.php\",\n" +
                "  \"message\" : \"Happy Birthday\",\n" +
                "  \"card_id\" : \"1\",\n" +
                "}";
        byte[] bytes = json.getBytes();

        when(request.getParts()).thenReturn(parts);
        when(congratulationService.getLinkList(any(), anyString(), anyString())).thenReturn(linkList);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(bytes);
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(RuntimeException.class).when(congratulationService).save(any());
        //when
        addCongratulationServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(request).getParts();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(congratulationService).save(any());
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}