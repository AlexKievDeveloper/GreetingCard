package com.greetingcard.web.security;

import com.greetingcard.web.security.jwt.JwtTokenVerifierFilter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(1000)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenVerifierFilter jwtTokenVerifierFilter;

    @SneakyThrows
    @Override
    protected void configure(HttpSecurity http) {

        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtTokenVerifierFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/v1/auth", "/api/v1/user/forgot_password", "/api/v1/user/verification/*", "/api/v1/card/*/card_link/*", "/ap/v1/user").permitAll()
                .antMatchers("/api/v1/**").authenticated();
    }

}
