package com.greetingcard.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan("com.greetingcard.web")
@PropertySource("classpath:application.properties")
public class WebApplicationContext implements WebMvcConfigurer {
    @Value("${bucketName}")
    private String bucketName;
    @Value("${region}")
    private String region;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/audio/**").addResourceLocations("https://" + bucketName + ".s3." + region + ".amazonaws.com/audio/");
        registry.addResourceHandler("/img/**").addResourceLocations("https://" + bucketName + ".s3." + region + ".amazonaws.com/img/");
        registry.addResourceHandler("/profile/**").addResourceLocations("https://" + bucketName + ".s3." + region + ".amazonaws.com/profile/");
        registry.addResourceHandler("/index.html").addResourceLocations("/build/index.html");
        registry.addResourceHandler("/manifest.json").addResourceLocations("/build/manifest.json");
        registry.addResourceHandler("/static/**").addResourceLocations("/build/static/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}

