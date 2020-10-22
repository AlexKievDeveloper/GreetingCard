package com.greetingcard;

import com.greetingcard.dao.jdbc.JdbcCardDao;
import com.greetingcard.dao.jdbc.JdbcCongratulationDao;
import com.greetingcard.dao.jdbc.JdbcUserDao;
import com.greetingcard.dao.jdbc.config.DataSourceFactory;
import com.greetingcard.security.DefaultSecurityService;
import com.greetingcard.service.impl.DefaultCardService;
import com.greetingcard.service.impl.DefaultCongratulationService;
import com.greetingcard.util.PropertyReader;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> CONTAINER = new HashMap<>();

    static {
        PropertyReader propertyReader = new PropertyReader();
        register("PropertyReader", propertyReader);

        DataSource dataSource = new DataSourceFactory(propertyReader).getDataSource();
        register("DataSource", dataSource);

        JdbcUserDao jdbcUserDao = new JdbcUserDao(dataSource);
        JdbcCardDao jdbcCardDao = new JdbcCardDao(dataSource);
        JdbcCongratulationDao jdbcCongratulationDao = new JdbcCongratulationDao(dataSource);

        DefaultSecurityService defaultSecurityService = new DefaultSecurityService(jdbcUserDao);
        register("DefaultSecurityService", defaultSecurityService);

        DefaultCardService defaultCardService = new DefaultCardService(jdbcCardDao);
        register("DefaultCardService", defaultCardService);

        DefaultCongratulationService defaultCongratulationService = new DefaultCongratulationService(jdbcCongratulationDao);
        register("DefaultCongratulationService", defaultCongratulationService);
    }

    public static <T> void register(String nameBean, T bean) {
        CONTAINER.put(nameBean, bean);
    }

    public static <T> T getBean(String nameBean) {
        return (T) CONTAINER.get(nameBean);
    }
}
