package com.greetingcard.dao.jdbc.config;

import org.apache.commons.dbcp2.BasicDataSource;
import com.greetingcard.util.PropertyReader;
import javax.sql.DataSource;

public class DataSorceFactory {
    private PropertyReader propertyReader;
    private DataSource dataSource;

    public DataSorceFactory(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public DataSource getDataSource() {
        dataSource = new BasicDataSource();
        dataSource.setUrl(propertyReader.getProperty("jdbc.url"));
        dataSource.setUsername(propertyReader.getProperty("jdbc.user"));
        dataSource.setPassword(propertyReader.getProperty("jdbc.password"));
        dataSource.setDriverClassName(propertyReader.getProperty("driver.name"));
        dataSource.setInitialSize(propertyReader.getProperty("connections.amount"));
        return dataSource;
    }
}
