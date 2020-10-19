package com.greetingcard.dao.jdbc;

import com.greetingcard.util.PropertyReader;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class DataBaseConfigurator {
    private PropertyReader propertyReader = new PropertyReader();

    public DataBaseConfigurator() {
        propertyReader.readProperties("/application-test.properties");
    }

    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(propertyReader.getProperty("jdbc.url"));
        dataSource.setUsername(propertyReader.getProperty("jdbc.user"));
        dataSource.setPassword(propertyReader.getProperty("jdbc.password"));
        dataSource.setDriverClassName(propertyReader.getProperty("jdbc.driver"));
        dataSource.setInitialSize(Integer.parseInt(propertyReader.getProperty("connections.amount")));
        return dataSource;
    }

    public Flyway getFlyway() {
        return Flyway.configure().dataSource(getDataSource()).locations("testDB/migration").load();
    }
}
