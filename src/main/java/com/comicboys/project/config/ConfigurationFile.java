package com.comicboys.project.config;

import java.net.*;
import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.*;



public class ConfigurationFile {
//    Map<String,String> config;
//    public ConfigurationFile() {
//        File myJson = new File("../../../../../../../config.json");
//        try {
//            config = new ObjectMapper().readValue(myJson, HashMap.class);
//        } catch (IOException e) {
//            System.out.println("Error loading JSON file");
//        }
//    }
//    public Map<String, String> getConfig() {
//        return config;
//    }

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
    public String get(String key) {
        return properties.get(key).toString();
    }
}