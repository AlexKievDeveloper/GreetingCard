package com.greetingcard.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class PropertyReader {
    private String defaultProdPropertiesPath = "/application.properties";
    private String defaultDevPropertiesPath = "/dev.properties";
    private String[] propertiesPath;
    private Properties properties;

    public PropertyReader() {
        this.propertiesPath = new String[]{defaultProdPropertiesPath};
        properties = getProperties();
    }

    public PropertyReader(String... propertiesPath) {
        this.propertiesPath = propertiesPath;
        properties = getProperties();
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        readProperties(properties);

        if (!("PROD").equals(System.getenv("env"))) {
            propertiesPath = new String[]{defaultDevPropertiesPath};
            readProperties(properties);
        }
        return properties;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    void readProperties(Properties properties) {
        for (String pathToPropertiesFile : propertiesPath) {
            try (val inputStream = getClass().getResourceAsStream(pathToPropertiesFile)) {
                properties.load(inputStream);
            } catch (IOException e) {
                log.error("Error while reading properties", e);
                throw new RuntimeException("Error while reading properties", e);
            }
        }
    }

    void setDefaultDevPropertiesPath(String defaultDevPropertiesPath) {
        this.defaultDevPropertiesPath = defaultDevPropertiesPath;
    }
}

