package com.greetingcard.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertyReaderITest {
    PropertyReader actualProperties = new PropertyReader();

    @Test
    @DisplayName("Returns complex properties from application.properties file")
    void getPropertiesDevEnvironmentTest() {
        //prepare
        actualProperties.readProperties("/application.properties");

        //then
        assertEquals("jdbc:postgresql://ec2-54-75-231-215.eu-west-1.compute.amazonaws.com:5432/dftoi6ipqclcmp?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory&", actualProperties.getProperty("jdbc.url"));
        assertEquals("sdpgpmzaoudjdz", actualProperties.getProperty("jdbc.user"));
        assertEquals("f66000b74bbc3005a1113405b2c6219dfb24603cee3f5a53e213fc40ac60ef35", actualProperties.getProperty("jdbc.password"));
        assertEquals("8080", actualProperties.getProperty("port"));
        assertEquals("true", actualProperties.getProperty("thymeleaf.cache"));
    }

    @Test
    @DisplayName("Returns properties from application.properties file when properties do not exist")
    void getPropertiesNull() {
        //prepare
        actualProperties.readProperties("/application.properties");

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
