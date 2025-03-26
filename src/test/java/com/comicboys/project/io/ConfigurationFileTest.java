package com.comicboys.project.io;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationFileTest {
    private ConfigurationFile testConfig;

    @BeforeEach
    public void setUp() {
        testConfig = new ConfigurationFile();
    }

    // if fails to read config, exception will be caught in constructor and properties remains null
    @Test
    public void readingConfigFileSuccessfully() {
        // if fails, properties are still null meaning config file was not read successfully
        assertFalse(testConfig.getProperties().isEmpty());
    }
    // if a property does not exist, should return null
    @Test
    public void gettingNullProperties() {
        String expectedKey = "NON_EXISTING_PROPERTY";
        // check for a property not in config file
        if (!testConfig.getProperties().contains(expectedKey)) {
            // expects null because we are accessing a key not in our properties (because its empty)
            assertNull(testConfig.getProperty(expectedKey));
        }
    }
    // if a property does exist, should not return null
    @Test
    public void gettingExistingProperties() {
        String expectedKey = "API_KEY";
        // setting as non null
        testConfig.setProperty(expectedKey, "");
        // since we set property, should not be null
        assertNotNull(testConfig.getProperty(expectedKey));
    }
    // should be able to set properties and read them back successfully
    @Test
    public void updatingProperties() {
        String expectedKey = "API_KEY";
        String expectedValue = "1";
        // setting out desired property
        testConfig.setProperty(expectedKey, expectedValue);
        // check if value we get is the one we set
        assertEquals(expectedValue, testConfig.getProperty(expectedKey));
    }
}