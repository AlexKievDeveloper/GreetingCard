package com.greetingcard.dao.jdbc.config;

import com.greetingcard.util.PropertyReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
@Slf4j
public class DataSourceFactory {
    private PropertyReader propertyReader;

    public DataSourceFactory(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(propertyReader.getProperty("jdbc.url"));
        dataSource.setUsername(propertyReader.getProperty("jdbc.user"));
        dataSource.setPassword(propertyReader.getProperty("jdbc.password"));
        dataSource.setDriverClassName(propertyReader.getProperty("jdbc.driver"));
        dataSource.setInitialSize(Integer.parseInt(propertyReader.getProperty("connections.amount")));

        log.info(propertyReader.getProperty("jdbc.url"));
        return dataSource;
    }
}
