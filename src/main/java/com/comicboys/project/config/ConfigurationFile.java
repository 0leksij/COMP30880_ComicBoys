package com.comicboys.project.config;

import java.io.*;
import java.util.*;

public class ConfigurationFile {
    // create properties object
    Properties properties = new Properties();
    public ConfigurationFile() {
        try {

            // create a reader object on the properties file
            FileReader reader = new FileReader("src/main/java/com/comicboys/project/config/config.properties");
            // Add a wrapper around reader object
            properties.load(reader);
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
    }
    public Properties getProperties() {
        return properties;
    }
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperties(String key, String value) {
        properties.setProperty(key, value);
    }
}