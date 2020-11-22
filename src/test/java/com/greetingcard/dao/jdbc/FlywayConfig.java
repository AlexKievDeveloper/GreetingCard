package com.greetingcard.dao.jdbc;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;

@ImportResource(locations = {"classpath:spring/applicationContext.xml","file:src/main/webapp/WEB-INF/dispatcher-servlet.xml"})
@Configuration
public class FlywayConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public Flyway flyway() {
        return Flyway.configure().dataSource(dataSource).locations("testDB/migration").baselineOnMigrate(true).load();
    }
}
