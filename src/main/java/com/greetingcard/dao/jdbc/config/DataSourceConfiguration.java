package com.greetingcard.dao.jdbc.config;

import org.apache.commons.dbcp2.BasicDataSource;
import com.greetingcard.util.PropertyReader;
import javax.sql.DataSource;

public class DataSourceConfiguration  extends BasicDataSource{
    private PropertyReader propertyReader;

    public DataSourceConfiguration(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(propertyReader.getProperty("jdbc.url"));
        dataSource.setUsername(propertyReader.getProperty("jdbc.user"));
        dataSource.setPassword(propertyReader.getProperty("jdbc.password"));
        dataSource.setDriverClassName(propertyReader.getProperty("driver.name"));
        dataSource.setInitialSize(propertyReader.getProperty("number.connections"));
        return dataSource;
    }
}
