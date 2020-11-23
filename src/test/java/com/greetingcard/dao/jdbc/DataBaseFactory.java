package com.greetingcard.dao.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ComponentScan(basePackages = "com.greetingcard")
@Configuration
public class DataBaseFactory {
    @Bean
    public DataSource dataSource() {
        return
                new EmbeddedDatabaseBuilder()
                        .setType(EmbeddedDatabaseType.H2)
                        .setName("jdbc:h2:mem:default;MODE=PostgreSQL")
                        .addScript("classpath:db/migration/V1_1__language_schema.sql")
                        .addScript("classpath:db/migration/V1_2__setUpLanguages.sql")
                        .addScript("classpath:db/migration/V2__user_schema.sql")
                        .addScript("classpath:db/migration/V3__statuses_schema.sql")
                        .addScript("classpath:db/migration/V3_1__setUpStatus.sql")
                        .addScript("classpath:db/migration/V4__roles_schema.sql")
                        .addScript("classpath:db/migration/V4_1__setUpRole.sql")
                        .addScript("classpath:db/migration/V5__cards_schema.sql")
                        .addScript("classpath:db/migration/V6__users_cards_schema.sql")
                        .addScript("classpath:db/migration/V7__congratulations_schema.sql")
                        .addScript("classpath:db/migration/V8__link_types_schema.sql")
                        .addScript("classpath:db/migration/V8_1__setUpTypes.sql")
                        .addScript("classpath:db/migration/V9__links_schema.sql")
                        .addScript("classpath:testDB/migration/R__1setUpUsers.sql")
                        .addScript("classpath:testDB/migration/R__2setUpCards.sql")
                        .addScript("classpath:testDB/migration/R__3setUpUsers_Cards.sql")
                        .addScript("classpath:testDB/migration/R__4setUpCongratulations.sql")
                        .addScript("classpath:testDB/migration/R__setUpContent.sql")
                        .build();
    }
}
