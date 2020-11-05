package com.greetingcard.web.servlet.congratulation;

import com.greetingcard.entity.Link;
import com.greetingcard.entity.Status;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CongratulationServletTest {
    @Mock
    private ServletInputStream servletInputStream;
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
    private CongratulationServlet congratulationServlet;

    @Test
    @DisplayName("Saving congratulation to DB")
    void doPostTest() throws IOException {
        //prepare
        String json = "{\n" +
                "  \"youtube\" : \"https://www.youtube.com/watch?v=r-0qNVT_I4s\",\n" +
                "  \"plain_link\" : \"https://www.studytonight.com/servlet/httpsession.php\",\n" +
                "  \"message\" : \"Happy Birthday\",\n" +
                "  \"card_id\" : \"1\",\n" +
                "}";
        byte[] bytes = json.getBytes();

        when(congratulationService.getLinkList(anyString(), anyString())).thenReturn(linkList);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(bytes);
        //when
        congratulationServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(congratulationService).save(any());
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Exception while saving congratulation to DB")
    void doPostExceptionTest() throws IOException {
        //prepare
        String json = "{\n" +
                "  \"youtube\" : \"https://www.youtube.com/watch?v=r-0qNVT_I4s\",\n" +
                "  \"plain_link\" : \"https://www.studytonight.com/servlet/httpsession.php\",\n" +
                "  \"message\" : \"Happy Birthday\",\n" +
                "  \"card_id\" : \"1\",\n" +
                "}";
        byte[] bytes = json.getBytes();

        when(congratulationService.getLinkList(anyString(), anyString())).thenReturn(linkList);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.readAllBytes()).thenReturn(bytes);
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(RuntimeException.class).when(congratulationService).save(any());
        //when
        congratulationServlet.doPost(request, response);
        //then
        verify(request).getInputStream();
        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(user).getId();
        verify(congratulationService).save(any());
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Changing congratulation status")
    void doPut() throws IOException {
        //prepare
        when(request.getPathInfo()).thenReturn("/api/v1/congratulation/1/status");
        //when
        congratulationServlet.doPut(request, response);
        //then
        verify(request).getPathInfo();
        verify(congratulationService).changeCongratulationStatusByCongratulationId(Status.ISOVER, 1);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    @DisplayName("Exception while changing congratulation status")
    void doPutException() throws IOException {
        //prepare
        when(request.getPathInfo()).thenReturn("/api/v1/congratulation/1/status");
        when(response.getWriter()).thenReturn(printWriter);
        doThrow(RuntimeException.class).when(congratulationService).changeCongratulationStatusByCongratulationId(Status.ISOVER, 1);
        //when
        congratulationServlet.doPut(request, response);
        //then
        verify(congratulationService).changeCongratulationStatusByCongratulationId(Status.ISOVER, 1);
        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Deleting congratulation by id")
    void doDelete() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getPathInfo()).thenReturn("/api/v1/congratulation/1");
        when(user.getId()).thenReturn(1L);
        when(user.getLogin()).thenReturn("user");

        congratulationServlet.doDelete(request, response);

        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(request).getPathInfo();
        verify(user).getId();
        verify(user, times(2)).getLogin();
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    @DisplayName("Throws Exception while deleting congratulation by id")
    void doDeleteException() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(request.getPathInfo()).thenReturn("/api/v1/congratulation/1");
        when(user.getId()).thenReturn(1L);
        when(user.getLogin()).thenReturn("user");
        doThrow(RuntimeException.class).when(congratulationService).deleteById(1L, 1L);

        congratulationServlet.doDelete(request, response);

        verify(request).getSession();
        verify(session).getAttribute("user");
        verify(request).getPathInfo();
        verify(user).getId();
        verify(user, times(2)).getLogin();
        verify(congratulationService).deleteById(1L, 1L);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}