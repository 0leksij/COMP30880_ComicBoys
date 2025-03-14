package com.comicboys.project;

import client.APIClient;
import com.comicboys.project.config.ConfigurationFile;


public class Main {
    public static void main(String[] args) {
        ConfigurationFile configFile = new ConfigurationFile();
        APIClient client = new APIClient(configFile);


        System.out.println(client.sendPrompt("Hello"));

    }
}