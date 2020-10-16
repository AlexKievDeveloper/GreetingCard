package com.greetingcard.dao.jdbc.config;

import org.apache.commons.dbcp2.BasicDataSource;
import com.greetingcard.util.PropertyReader;

import javax.sql.DataSource;

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
        return dataSource;
    }
}
