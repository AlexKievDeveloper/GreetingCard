package com.greetingcard.web.listener;

import com.greetingcard.ServiceLocator;
import com.greetingcard.security.DefaultSecurityService;
import com.greetingcard.web.templater.PageGenerator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThymeleafInitializerListener implements ServletContextListener {
    private DefaultSecurityService securityService = ServiceLocator.getBean("DefaultSecurityService");

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        PageGenerator.instance().configTemplate(servletContext);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(securityService, 0, 10, TimeUnit.MINUTES);
    }
}
