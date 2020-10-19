package com.greetingcard.web.templater;

import com.greetingcard.ServiceLocator;
import com.greetingcard.util.PropertyReader;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class PageGenerator {
    private static final PageGenerator INSTANCE = new PageGenerator();
    private final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine();
    private boolean isConfigured;

    public static PageGenerator getInstance() {
        return INSTANCE;
    }

    private PageGenerator() {
    }

    public synchronized void configTemplate(ServletContext servletContext) {
        if (isConfigured) {
            return;
        }
        PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setPrefix(propertyReader.getProperty("thymeleaf.prefix"));
        templateResolver.setSuffix(propertyReader.getProperty("thymeleaf.suffix"));
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(Boolean.parseBoolean(propertyReader.getProperty("thymeleaf.cache")));
        TEMPLATE_ENGINE.setTemplateResolver(templateResolver);
        isConfigured = true;
    }

    public void process(String template, HttpServletRequest request, HttpServletResponse response) throws IOException {
        process(template,Collections.emptyMap(), request, response);
    }

    public void process(String template, Map<String, Object> productMap, HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),
                productMap);
        TEMPLATE_ENGINE.process(template, context, response.getWriter());
    }
}

