package com.greetingcard.util;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyReaderITest {
    private Map<String, String> propertiesMap;

    private PropertyReaderITest() {
        propertiesMap = new HashMap<>();
    }

    @Test
    @DisplayName("Returns complex properties from application-test.properties file and dev.properties file (environment is DEV)")
    void getPropertiesDevEnvironmentTest() {
        //prepare
        PropertyReader propertyReader = new PropertyReader("/application-test.properties");
        propertyReader.setDefaultDevPropertiesPath("/dev-test.properties");
        propertiesMap.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card1");
        propertiesMap.put("db.user", "postgres1");
        propertiesMap.put("db.password", "postgres1");
        propertiesMap.put("port", "8080");
        propertiesMap.put("thymeleaf.cache", "false");

        //when
        val properties = propertyReader.getProperties();

        //then
        assertEquals(propertiesMap.get("db.url"), properties.getProperty("db.url"));
        assertEquals(propertiesMap.get("db.user"), properties.getProperty("db.user"));
        assertEquals(propertiesMap.get("db.password"), properties.getProperty("db.password"));
        assertEquals(propertiesMap.get("port"), properties.getProperty("port"));
        assertEquals(propertiesMap.get("thymeleaf.cache"), properties.getProperty("thymeleaf.cache"));
    }

    @Test
    @DisplayName("Returns properties from application-test.properties (environment is PROD)")
    void getPropertiesProdEnvironment() throws Exception {
        //prepare
        withEnvironmentVariable("env", "PROD").execute(this::getPropertiesProdEnvironmentTest);
    }

    void getPropertiesProdEnvironmentTest() {
        //prepare
        PropertyReader propertyReader = new PropertyReader("/application-test.properties");
        propertiesMap.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card");
        propertiesMap.put("db.user", "postgres");
        propertiesMap.put("db.password", "postgres");
        propertiesMap.put("port", "8080");
        propertiesMap.put("thymeleaf.cache", "true");

        //when
        val properties = propertyReader.getProperties();

        //then
        assertEquals(propertiesMap.get("db.url"), properties.getProperty("db.url"));
        assertEquals(propertiesMap.get("db.user"), properties.getProperty("db.user"));
        assertEquals(propertiesMap.get("db.password"), properties.getProperty("db.password"));
        assertEquals(propertiesMap.get("port"), properties.getProperty("port"));
        assertEquals(propertiesMap.get("thymeleaf.cache"), properties.getProperty("thymeleaf.cache"));
    }

    @Test
    @DisplayName("Read properties from application-test.properties")
    void readPropertiesProdEnvironment() throws Exception {
        //prepare
        withEnvironmentVariable("env", "PROD").execute(this::readPropertiesTest);
    }

    void readPropertiesTest() {
        //prepare
        PropertyReader propertyReader = new PropertyReader("/application-test.properties");
        Properties properties = new Properties();
        propertiesMap.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card");
        propertiesMap.put("db.user", "postgres");
        propertiesMap.put("db.password", "postgres");
        propertiesMap.put("port", "8080");
        propertiesMap.put("thymeleaf.cache", "true");

        //when
        propertyReader.readProperties(properties);

        //then
        assertEquals(propertiesMap.get("db.url"), properties.getProperty("db.url"));
        assertEquals(propertiesMap.get("db.user"), properties.getProperty("db.user"));
        assertEquals(propertiesMap.get("db.password"), properties.getProperty("db.password"));
        assertEquals(propertiesMap.get("port"), properties.getProperty("port"));
        assertEquals(propertiesMap.get("thymeleaf.cache"), properties.getProperty("thymeleaf.cache"));
    }

    @Test
    @DisplayName("Returns property")
    void getPropertyProdEnvironment() throws Exception {
        //prepare
        withEnvironmentVariable("env", "PROD").execute(this::getPropertyTest);
    }

    void getPropertyTest() {
        //prepare
        PropertyReader propertyReader = new PropertyReader("/application-test.properties");

        //when
        String actual = propertyReader.getProperty("thymeleaf.cache");

        //then
        assertEquals("true", actual);
    }
}
