package com.greetingcard;

import com.greetingcard.web.WebApplicationContext;
import com.greetingcard.web.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

@PropertySource("classpath:application.properties")
public class GreetingCardAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Value("${max.upload.file.size}")
    private int maxUploadFileSizeInMb;

    @Value("${max.upload.request.size}")
    private int maxUploadRequestSizeInMb;

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootApplicationContext.class};
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
        return new Filter[]{new AuthorizationFilter(), encodingFilter};
    }

    private MultipartConfigElement getMultipartConfigElement() {
        return new MultipartConfigElement("", maxUploadFileSizeInMb, maxUploadRequestSizeInMb, 0);
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
    }
}
