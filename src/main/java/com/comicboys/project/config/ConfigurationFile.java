package com.comicboys.project.config;

import java.io.*;
import java.util.*;

public class ConfigurationFile {
    // file path
    String configFilePath = "src/main/java/com/comicboys/project/config/config.properties";
    // create properties object
    Properties properties = new Properties();
    public ConfigurationFile() {
        try {
            // create a reader object on the properties file
            FileReader reader = new FileReader(configFilePath);
            // Add a wrapper around reader object
            properties.load(reader);
        } catch (IOException e) {
            System.out.println("Failed to read config file from path: " + configFilePath + "Check if config.properties is missing");
        }
    }
    public Properties getProperties() {
        return properties;
    }
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    public void setProperties(Properties newProperties) {
        this.properties = new Properties();
        for (Object key : newProperties.keySet()) {
            this.properties.setProperty(key.toString(), newProperties.getProperty(key.toString()));
        }
    };
    public void setProperty(String key, String value) { properties.setProperty(key, value); }
}