package com.greetingcard;

import com.greetingcard.dao.jdbc.config.DataSorceFactory;
import com.greetingcard.util.PropertyReader;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> CONTAINER = new HashMap<>();

    static {
        PropertyReader propertyReader = new PropertyReader();
        register("PropertyReader", propertyReader);

        DataSource dataSource = new DataSorceFactory(propertyReader).getDataSource();
        register("DataSource", dataSource);
    }

    public static <T> void register(String nameBean, T bean) {
        CONTAINER.put(nameBean, bean);
    }

    public static <T> T getBean(String nameBean) {
        return (T) CONTAINER.get(nameBean);
    }
}
