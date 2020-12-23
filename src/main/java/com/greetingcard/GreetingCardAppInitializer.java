package com.greetingcard;

import com.greetingcard.web.WebApplicationContext;
import com.greetingcard.web.security.ApplicationSecurityConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class GreetingCardAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private final int maxUploadFileSizeInMb = 1024 * 1024 * 10;
    private final int maxUploadRequestSizeInMb = 1024 * 1024 * 50;

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootApplicationContext.class, ApplicationSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebApplicationContext.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/*"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return new Filter[]{encodingFilter};
    }

    private MultipartConfigElement getMultipartConfigElement() {
        return new MultipartConfigElement("", maxUploadFileSizeInMb, maxUploadRequestSizeInMb, 0);
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
    }
}
