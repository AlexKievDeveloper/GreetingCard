package com.greetingcard.util;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertyReaderITest {
    PropertyReader actualProperties = new PropertyReader();

    @Test
    @DisplayName("Returns complex properties from application-test.properties file")
    void getPropertiesDevEnvironmentTest() {
        //prepare
        actualProperties.readProperties("/application-test.properties");

        //then
        assertEquals("jdbc:postgresql://ec2-18-203-62-227.eu-west-1.compute.amazonaws.com:5432/d1juchadlrojvc?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory&", actualProperties.getProperty("jdbc.url"));
        assertEquals("avvtjpmzlrnonj", actualProperties.getProperty("jdbc.user"));
        assertEquals("342c0504c190f09e55eb9041b4edf7658b30ff56bc9ec8c0cb0fdb69aa081236", actualProperties.getProperty("jdbc.password"));
        assertEquals("8080", actualProperties.getProperty("port"));
        assertEquals("true", actualProperties.getProperty("thymeleaf.cache"));
    }

    @Test
    @DisplayName("Returns properties from application-test.properties file when properties do not exist")
    void getPropertiesNull() {
        //prepare
        actualProperties.readProperties("/application-test.properties");

        //then
        assertEquals("", actualProperties.getProperty("nothing"));
        assertNull(actualProperties.getProperty("abracadabra"));
    }

    @Test
    @DisplayName("Returns system environment")
    void getPropertiesProdEnvironment() throws Exception {
        //prepare
        withEnvironmentVariable("env", "PROD").execute(this::getPropertiesProdEnvironmentTest);
    }

    void getPropertiesProdEnvironmentTest() {
        assertEquals("PROD", actualProperties.getProperty("env"));
    }
}
