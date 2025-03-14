package com.comicboys.project.config;

//import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.*;

import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigurationFileTest {

    // if fails to read config, exception will be caught in constructor and properties remains null
    @Test
    public void readingConfigFileSuccessfully() {
        ConfigurationFile testConfig = new ConfigurationFile();
        // if fails, properties are still null meaning config file was not read successfully
        assertFalse(testConfig.getProperties().isEmpty());
    }
    // if a property does not exist, should return null
    @Test
    public void gettingNullProperties() {
        ConfigurationFile testConfig = new ConfigurationFile();
        Properties newProperties = new Properties();
        String expectedKey = "API_KEY";
        // new properties will be our empty properties
        testConfig.setProperties(newProperties);
        // expects null because we are accessing a key not in our properties (because its empty)
        assertNull(testConfig.getProperty(expectedKey));
    }
    // if a property does exist, should not return null
    @Test
    public void gettingExistingProperties() {
        ConfigurationFile testConfig = new ConfigurationFile();
        String expectedKey = "API_KEY";
        // setting as non null
        testConfig.setProperty(expectedKey, "");
        // since we set property, should not be null
        assertNotNull(testConfig.getProperty(expectedKey));
    }
    // should be able to set properties and read them back successfully
    @Test
    public void updatingProperties() {
        ConfigurationFile testConfig = new ConfigurationFile();
        String expectedKey = "API_KEY";
        String expectedValue = "1";
        // setting out desired property
        testConfig.setProperty(expectedKey, expectedValue);
        // check if value we get is the one we set
        assertEquals(expectedValue, testConfig.getProperty(expectedKey));
    }
}