package com.greetingcard.dao.jdbc;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;

@ImportResource(locations = {"classpath:spring/applicationContext.xml", "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml"})
@Configuration
public class TestConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public Flyway configureDataSource(){
        return Flyway.configure().dataSource(dataSource)
                .locations("classpath:db/migration").baselineOnMigrate(true).load();
    }
}
