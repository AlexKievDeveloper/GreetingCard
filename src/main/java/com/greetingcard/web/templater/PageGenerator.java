package com.greetingcard.web.templater;

import com.greetingcard.ServiceLocator;
import com.greetingcard.util.PropertyReader;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class PageGenerator {
    private static PageGenerator pageGenerator;
    private final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine();
    private boolean isConfigured;

    public static PageGenerator instance() {
        if (pageGenerator == null) {
            pageGenerator = new PageGenerator();
        }
        return pageGenerator;
    }

    private PageGenerator() {
    }

    public synchronized void configTemplate(ServletContext servletContext) {
        if (isConfigured) {
            return;
        }
        isConfigured = true;
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        PropertyReader propertyReader = ServiceLocator.getBean("PropertyReader");

        Properties properties = propertyReader.getProperties();

        templateResolver.setPrefix(properties.getProperty("thymeleaf.prefix"));
        templateResolver.setSuffix(properties.getProperty("thymeleaf.suffix"));
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(Boolean.parseBoolean(properties.getProperty("thymeleaf.cache")));
        TEMPLATE_ENGINE.setTemplateResolver(templateResolver);
    }

    public void process(String template, Map<String, Object> productMap, Writer writer) {
        Context context = new Context(Locale.getDefault(), productMap);
        TEMPLATE_ENGINE.process(template, context, writer);
    }

    public void process(String template, Writer writer) {
        process(template, Collections.emptyMap(), writer);
    }

    public void process(String template, Writer writer, HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale());
        TEMPLATE_ENGINE.process(template, context, writer);
    }
}
