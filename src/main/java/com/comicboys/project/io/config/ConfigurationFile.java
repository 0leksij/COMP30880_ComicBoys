package com.comicboys.project.io.config;

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
            // in the case that properties fails to load (or we use a custom getProperty like we do in TranslationGeneratorTest)
            try {
                // ensure lesson schedule is set to default if undefined
                if (getProperty("LESSON_SCHEDULE") == null || getProperty("LESSON_SCHEDULE").replace(" ", "").isEmpty()) {
                    setProperty("LESSON_SCHEDULE", "conjugation,left,whole,story,left,whole,conjugation,left,conjugation,whole,story");
                } else {
                    // otherwise use custom lesson schedule, ensuring is formatted correctly
                    setProperty("LESSON_SCHEDULE", getProperty("LESSON_SCHEDULE").toLowerCase().replace(" ",""));
                }
            } catch (NullPointerException e) {
                System.out.println("\nLesson Schedule config property failed to load");
            }
            try {

                // ensure whether audio is generated is defined or if is set to true
                if (getProperty("GENERATE_AUDIO") == null || !Objects.equals(getProperty("GENERATE_AUDIO").toLowerCase(), "true")) {
                    setProperty("GENERATE_AUDIO", "false");
                } else {
                    // otherwise make sure is lower-case
                    setProperty("GENERATE_AUDIO", getProperty("GENERATE_AUDIO").toLowerCase());
                }
            } catch (NullPointerException e) {
                System.out.println("\nGenerate Audio config property failed to load");
            }
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