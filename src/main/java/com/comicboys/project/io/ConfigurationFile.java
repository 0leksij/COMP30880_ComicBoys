package com.comicboys.project.io;

import java.io.*;
import java.util.*;

public class ConfigurationFile {
    // file path
    String configFilePath = "assets/config/config.properties";
    // create properties object
    Properties properties = new Properties();
    public ConfigurationFile() {
        try {
            // create a reader object on the properties file
            FileReader reader = new FileReader(configFilePath);
            // Add a wrapper around reader object
            properties.load(reader);
        } catch (IOException e) {
            System.out.println("\nFailed to read config file from path: " + configFilePath + "\nCheck if config.properties is missing");
        }
    }
    // getters
    public Properties getProperties() {
        return properties;
    }
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    // setters
    public void setProperty(String key, String value) { properties.setProperty(key, value); }
}