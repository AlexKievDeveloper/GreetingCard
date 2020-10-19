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
    private Map<String, String> expectedProperties;

    private PropertyReaderITest() {
        expectedProperties = new HashMap<>();
    }

    @Test
    @DisplayName("Returns complex properties from application-test.properties file and dev.properties file " +
            "(environment is DEV)")
    void getPropertiesDevEnvironmentTest() {
        //prepare
        PropertyReader propertyReader = new PropertyReader("/application-test.properties");
        propertyReader.setDevPropertiesPath("/dev-test.properties");
        expectedProperties.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card1");
        expectedProperties.put("db.user", "postgres1");
        expectedProperties.put("db.password", "postgres1");
        expectedProperties.put("port", "8080");
        expectedProperties.put("thymeleaf.cache", "false");

        //when
        val actualProperties = propertyReader.getProperties();

        //then
        assertEquals(expectedProperties.get("db.url"), actualProperties.getProperty("db.url"));
        assertEquals(expectedProperties.get("db.user"), actualProperties.getProperty("db.user"));
        assertEquals(expectedProperties.get("db.password"), actualProperties.getProperty("db.password"));
        assertEquals(expectedProperties.get("port"), actualProperties.getProperty("port"));
        assertEquals(expectedProperties.get("thymeleaf.cache"), actualProperties.getProperty("thymeleaf.cache"));
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
        expectedProperties.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card");
        expectedProperties.put("db.user", "postgres");
        expectedProperties.put("db.password", "postgres");
        expectedProperties.put("port", "8080");
        expectedProperties.put("thymeleaf.cache", "true");

        //when
        Properties actualProperties = propertyReader.getProperties();

        //then
        assertEquals(expectedProperties.get("db.url"), actualProperties.getProperty("db.url"));
        assertEquals(expectedProperties.get("db.user"), actualProperties.getProperty("db.user"));
        assertEquals(expectedProperties.get("db.password"), actualProperties.getProperty("db.password"));
        assertEquals(expectedProperties.get("port"), actualProperties.getProperty("port"));
        assertEquals(expectedProperties.get("thymeleaf.cache"), actualProperties.getProperty("thymeleaf.cache"));
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
        Properties actualProperties = new Properties();
        expectedProperties.put("db.url", "jdbc:postgresql://localhost:5432/greeting-card");
        expectedProperties.put("db.user", "postgres");
        expectedProperties.put("db.password", "postgres");
        expectedProperties.put("port", "8080");
        expectedProperties.put("thymeleaf.cache", "true");

        //when
        propertyReader.readProperties(actualProperties);

        //then
        assertEquals(expectedProperties.get("db.url"), actualProperties.getProperty("db.url"));
        assertEquals(expectedProperties.get("db.user"), actualProperties.getProperty("db.user"));
        assertEquals(expectedProperties.get("db.password"), actualProperties.getProperty("db.password"));
        assertEquals(expectedProperties.get("port"), actualProperties.getProperty("port"));
        assertEquals(expectedProperties.get("thymeleaf.cache"), actualProperties.getProperty("thymeleaf.cache"));
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
        String expectedUrl = "jdbc:postgresql://localhost:5432/greeting-card";
        String expectedUser = "postgres";
        String expectedPassword = "postgres";
        String expectedPort = "8080";
        String expectedThymeleafCache = "true";

        //when
        String actualUrl = propertyReader.getProperty("db.url");
        String actualUser = propertyReader.getProperty("db.user");
        String actualPassword = propertyReader.getProperty("db.password");
        String actualPort = propertyReader.getProperty("port");
        String actualThymeleafCache = propertyReader.getProperty("thymeleaf.cache");

        //then
        assertEquals(expectedUrl, actualUrl);
        assertEquals(expectedUser, actualUser);
        assertEquals(expectedPassword, actualPassword);
        assertEquals(expectedPort, actualPort);
        assertEquals(expectedThymeleafCache, actualThymeleafCache);
    }
}
