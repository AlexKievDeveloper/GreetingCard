package com.greetingcard.dao.jdbc;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public Flyway flyway(){
        return Flyway.configure().dataSource(dataSource).locations("testDB/migration").baselineOnMigrate(true).load();
    }
}
