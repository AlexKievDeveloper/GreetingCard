package com.greetingcard.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class PropertyReader {
    private static final Properties properties = new Properties();

    public PropertyReader() {
        readProperties("/application.properties");
        if (!("PROD").equals(System.getenv("env"))) {
            readProperties("/dev.properties");
        }
    }

    public void readProperties(String pathToFile) {
        try (InputStream inputStream = getClass().getResourceAsStream(pathToFile)) {
            if (inputStream == null) {
                log.error("Resource does not exist - {}", pathToFile);
                return;
            }
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Error while reading properties", e);
            throw new RuntimeException("Error while reading properties", e);
        }
    }

    public String getProperty(String propertyName) {
        String value;
        if ((value = properties.getProperty(propertyName)) != null) {
            return value;
        }
        if ((value = System.getenv(propertyName)) != null) {
            properties.put(propertyName, value);
            return value;
        }
        return null;
    }
}

