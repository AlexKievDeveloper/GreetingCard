package com.greetingcard.web.servlet.congratulation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddCongratulationServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    @Mock
    private ServletContext context;

    @Test
    @DisplayName("Returns add congratulation form")
    void doGet() throws IOException {
        //prepare
        AddCongratulationServlet addCongratulationServlet = new AddCongratulationServlet();
        when(request.getServletContext()).thenReturn(context);
        when(response.getWriter()).thenReturn(writer);
        //when
        addCongratulationServlet.doGet(request, response);
        //then
        verify(request).getServletContext();
        verify(request).getLocale();
        verify(response).getWriter();
    }
}