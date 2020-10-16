package com.greetingcard.web.listener;

import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ThymeleafInitializerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        PageGenerator.instance().configTemplate(servletContext);
    }
}
